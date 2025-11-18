package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {
        // This method is intentionally left empty.
        // Spring Security's form login mechanism intercepts the request before it reaches the controller
        // and handles authentication. The presence of this endpoint is to define the API contract for login.
        // The actual authentication is configured in SecurityConfig.
    }
}