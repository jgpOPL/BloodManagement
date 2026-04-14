package com.example.bloodmanagement.service.implementation;

import com.example.bloodmanagement.custom.NoUserFoundException;
import com.example.bloodmanagement.domain.PasswordHistory;
import com.example.bloodmanagement.domain.PasswordResetToken;
import com.example.bloodmanagement.domain.Users;
import com.example.bloodmanagement.dto.requestDto.ForgotPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.LoginRequestDto;
import com.example.bloodmanagement.dto.requestDto.ResetPasswordRequestDto;
import com.example.bloodmanagement.dto.requestDto.UserRequestDto;
import com.example.bloodmanagement.dto.requestDto.VerifyOtpReq;
import com.example.bloodmanagement.dto.responseDto.LoginResponseDto;
import com.example.bloodmanagement.repository.PasswordHistoryRepository;
import com.example.bloodmanagement.repository.PasswordResetTokenRepository;
import com.example.bloodmanagement.repository.UsersRepository;
import com.example.bloodmanagement.service.AuthService;
import com.example.bloodmanagement.utitity.JWTUtil;
import com.example.bloodmanagement.utitity.MapperUtility;
import com.example.bloodmanagement.utitity.TokenHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private UsersRepository userRepo;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private MapperUtility mapper;
    @Autowired private PasswordEncoder encoder;
    @Autowired private AuthenticationManager authManager;
    @Autowired private MyUserDetailsServiceImpl myUserDetails;
    @Autowired private PasswordResetTokenRepository resetTokenRepo;
    @Autowired private PasswordHistoryRepository passwordHistoryRepo;
    @Autowired private JavaMailSender mailSender;

    @Value("${app.reset-password.base-url:http://localhost:4201/reset-password}")
    private String resetBaseUrl;

    @Value("${app.reset-password.expiry-minutes:10}")
    private int expiryMinutes;

    // Legacy in-memory OTP (kept for backward compatibility)
    private String OTP;

    @Override
    public String registerUser(UserRequestDto dto) {
        Optional<Users> byUsername = userRepo.findByUsername(dto.getUsername());
        if (byUsername.isPresent())
            throw new NoUserFoundException("User already registered", HttpStatus.FOUND.value());
        Users user = mapper.dtoToEntityUser(dto);
        user.setPassword(encoder.encode(dto.getPassword()));
        userRepo.save(user);
        return "User registered successfully";
    }

    private static final int MAX_ATTEMPTS = 3;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto req) {
        Optional<Users> optUser = userRepo.findByUsername(req.getUsername());
        if (optUser.isEmpty()) {
            throw new NoUserFoundException("Invalid username or password", HttpStatus.UNAUTHORIZED.value());
        }

        Users user = optUser.get();

        // Check if currently locked
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            LoginResponseDto resp = new LoginResponseDto();
            resp.setLocked(true);
            resp.setLockedUntilEpochMs(user.getLockedUntil().toInstant(ZoneOffset.UTC).toEpochMilli());
            resp.setFailedAttempts(user.getFailedAttempts());
            return resp;
        }

        // Lock expired — reset
        if (user.getLockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
        }

        try {
            Authentication authenticate = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

            if (authenticate.isAuthenticated()) {
                user.setFailedAttempts(0);
                user.setLockedUntil(null);
                userRepo.save(user);

                UserDetails userDetails = myUserDetails.loadUserByUsername(req.getUsername());
                LoginResponseDto resp = new LoginResponseDto();
                resp.setToken(jwtUtil.generateToken(userDetails));
                resp.setRole(userDetails.getAuthorities().toString());
                resp.setUsername(req.getUsername());
                resp.setLocked(false);
                return resp;
            }
        } catch (BadCredentialsException ex) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                // Lock until end of today (11:59:59 PM)
                LocalDateTime endOfDay = LocalDateTime.now()
                        .withHour(23).withMinute(59).withSecond(59).withNano(0);
                user.setLockedUntil(endOfDay);
                userRepo.save(user);

                LoginResponseDto resp = new LoginResponseDto();
                resp.setLocked(true);
                resp.setLockedUntilEpochMs(endOfDay.toInstant(ZoneOffset.UTC).toEpochMilli());
                resp.setFailedAttempts(attempts);
                return resp;
            }

            userRepo.save(user);

            LoginResponseDto resp = new LoginResponseDto();
            resp.setLocked(false);
            resp.setFailedAttempts(attempts);
            return resp;
        }

        LoginResponseDto resp = new LoginResponseDto();
        resp.setLocked(false);
        return resp;
    }

    // ── Email link-based password reset ──────────────────────────────────────

    @Override
    @Transactional
    public String sendPasswordResetLink(ForgotPasswordRequestDto dto) {
        Users user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NoUserFoundException("No account found with that email", HttpStatus.NOT_FOUND.value()));

        // Invalidate any existing tokens for this user
        resetTokenRepo.deleteAllByUserId(user.getId());

        // Generate a secure random token and store its hash
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = TokenHashUtil.sha256(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        resetToken.setUsed(false);
        resetTokenRepo.save(resetToken);

        // Send email with the raw token in the link
        String resetLink = resetBaseUrl + "?token=" + rawToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Blood Management Password Reset Request");
        message.setText("Click the link below to reset your password. This link expires in "
                + expiryMinutes + " minute(s).\n\n" + resetLink
                + "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);

        return "Password reset link sent to your email";
    }

    @Override
    public String verifyResetToken(String token) {
        String tokenHash = TokenHashUtil.sha256(token);
        PasswordResetToken resetToken = resetTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new NoUserFoundException("Invalid or expired reset token", HttpStatus.BAD_REQUEST.value()));

        if (resetToken.isUsed())
            throw new NoUserFoundException("Reset token has already been used", HttpStatus.BAD_REQUEST.value());
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new NoUserFoundException("Reset token has expired", HttpStatus.BAD_REQUEST.value());

        return "Token is valid";
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequestDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new NoUserFoundException("Passwords do not match", HttpStatus.BAD_REQUEST.value());

        String tokenHash = TokenHashUtil.sha256(dto.getToken());
        PasswordResetToken resetToken = resetTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new NoUserFoundException("Invalid token", HttpStatus.BAD_REQUEST.value()));

        if (resetToken.isUsed())
            throw new NoUserFoundException("Reset token has already been used", HttpStatus.BAD_REQUEST.value());
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new NoUserFoundException("Reset token has expired", HttpStatus.BAD_REQUEST.value());

        Users user = resetToken.getUser();

        // Check against last 3 passwords
        List<PasswordHistory> history = passwordHistoryRepo.findTop3ByUserIdOrderByCreatedAtDesc(user.getId());
        for (PasswordHistory ph : history) {
            if (encoder.matches(dto.getNewPassword(), ph.getPasswordHash())) {
                throw new NoUserFoundException(
                    "New password cannot be the same as any of your last 3 passwords",
                    HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        // Mark token as used
        resetToken.setUsed(true);
        resetTokenRepo.save(resetToken);

        // If already 3 history records, delete the oldest before adding new
        if (passwordHistoryRepo.countByUserId(user.getId()) >= 3) {
            passwordHistoryRepo.deleteOldestByUserId(user.getId());
        }

        // Save new password to history
        PasswordHistory newHistory = new PasswordHistory();
        newHistory.setUser(user);
        newHistory.setPasswordHash(encoder.encode(dto.getNewPassword()));
        newHistory.setCreatedAt(LocalDateTime.now());
        passwordHistoryRepo.save(newHistory);

        // Update user password
        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return "Password reset successfully";
    }

    // ── Legacy OTP methods ────────────────────────────────────────────────────

    @Override
    public String forgotPassword(LoginRequestDto l) {
        userRepo.findByEmail(l.getUsername())
                .orElseThrow(() -> new NoUserFoundException("Username not found", HttpStatus.NOT_FOUND.value()));
        Random random = new Random();
        OTP = String.valueOf(100000 + random.nextInt(900000));
        return OTP;
    }

    @Override
    public String verifyOtp(VerifyOtpReq verifyOtpReq) {
        if (verifyOtpReq.getUsername() == null || verifyOtpReq.getOtp() == null)
            return "Username and Otp should not be null";
        Users user = userRepo.findByUsername(verifyOtpReq.getUsername()).get();
        if (OTP.equals(verifyOtpReq.getOtp())) {
            user.setPassword(encoder.encode(verifyOtpReq.getPassword()));
            userRepo.save(user);
            return "Password updated successfully";
        }
        return "Invalid otp";
    }
}
