package com.leo.cathay.account.service;

import com.leo.cathay.security.entity.UserAccount;
import com.leo.cathay.security.repository.UserAccountRepository;
import com.leo.cathay.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    public String login(String userName, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password) // 更正: 直接使用傳入的參數
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenUtil.generateToken(userDetails.getUsername());
    }

    public void registerNewUser(String userName, String password) {

        Optional<UserAccount> existingUser = userAccountRepository.findByUserName(userName);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }

        UserAccount newUser = new UserAccount();
        newUser.setUserName(userName);
        newUser.setPassword(passwordEncoder.encode(password));

        userAccountRepository.save(newUser);
    }
}