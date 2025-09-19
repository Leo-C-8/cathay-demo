package com.leo.cathay.account.controller;

import com.leo.cathay.account.dto.JwtResponse;
import com.leo.cathay.account.dto.LoginRequest;
import com.leo.cathay.account.dto.RegisterRequest;
import com.leo.cathay.account.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    UserAccountService userAccountService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = userAccountService.login(loginRequest.getUserName(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponse(loginRequest.getUserName(), jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤的帳號或密碼");
        }
    }

    @PostMapping("/registry")
    public ResponseEntity<?> registry(@RequestBody RegisterRequest registerRequest) {
        try {
            userAccountService.registerNewUser(registerRequest.getUserName(), registerRequest.getPassword());

            String jwt = userAccountService.login(registerRequest.getUserName(), registerRequest.getPassword());

            return ResponseEntity.ok(new JwtResponse(registerRequest.getUserName(), jwt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed.");
        }
    }
}