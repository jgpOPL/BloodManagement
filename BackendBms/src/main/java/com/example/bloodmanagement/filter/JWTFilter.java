package com.example.bloodmanagement.filter;

import com.example.bloodmanagement.service.implementation.MyUserDetailsServiceImpl;
import com.example.bloodmanagement.utitity.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsServiceImpl myUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader("Authorization");

        if(authToken!=null && authToken.startsWith("Bearer "))
        {
            String jwtToken = authToken.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);

            UserDetails userDetails = myUserService.loadUserByUsername(username);
            List<String> roles = jwtUtil.extractClaim(jwtToken, claims -> claims.get("role", ArrayList.class));
            List<SimpleGrantedAuthority> simpleGrantedAuthority = roles.stream().map(SimpleGrantedAuthority::new).toList();

            if(userDetails != null && SecurityContextHolder.getContext().getAuthentication()==null)
            {
                if(jwtUtil.validateToken(jwtToken,userDetails))
                {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,null,simpleGrantedAuthority);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
