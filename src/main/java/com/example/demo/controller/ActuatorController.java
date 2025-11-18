package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorController {

    @GetMapping("/actuator/custom")
    public String customEndpoint() {
        return "This is a custom actuator endpoint.";
    }
}