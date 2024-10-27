package com.juansecu.restart.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthApiController {
    @PostMapping("/register")
    public void register() {
        // Register a new user
    }
}
