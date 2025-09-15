package com.leo.cathay.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserEntity userEntity = userRepository.findByUsername(username);
        // if (userEntity == null) {
        //     throw new UsernameNotFoundException("User not found with username: " + username);
        // }

        if ("test".equals(username)) {
            // 密碼需要經過 BCryptPasswordEncoder 加密
            String encodedPassword = passwordEncoder.encode("password");

            return new User("test", encodedPassword, Collections.emptyList());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
