package com.example.bloodmanagement.service.implementation;

import com.example.bloodmanagement.domain.Users;
import com.example.bloodmanagement.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UsersRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> byName = userRepo.findByUsername(username);
        if(byName.isEmpty())
            throw new UsernameNotFoundException("User not found");

        String[] split = byName.get().getRole().split(",");
        List<SimpleGrantedAuthority> list = Arrays.stream(split).map(SimpleGrantedAuthority::new).toList();
        Users u1 = byName.get();
        return User.builder()
                .username(u1.getUsername())
                .password(u1.getPassword())
                .authorities(list)
                .build();
    }
}
