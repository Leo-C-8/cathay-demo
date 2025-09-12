package com.leo.cathay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/jwt")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("恭喜！你已成功透過 JWT 驗證，並存取了這個受保護的 API。");
    }
}
