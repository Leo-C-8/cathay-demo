package com.leo.cathay.controller;

import com.leo.cathay.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserDto user){
        return ResponseEntity.ok(user);
    }
}
