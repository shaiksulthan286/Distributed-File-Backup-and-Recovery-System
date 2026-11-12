package com.dfbrs.controller;

import com.dfbrs.dto.LoginRequest;
import com.dfbrs.dto.RegisterRequest;
import com.dfbrs.entity.User;
import com.dfbrs.service.UserService;
import com.dfbrs.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication controller exposing registration and login endpoints.
 * Both endpoints return a JWT token that must be supplied as `Authorization: Bearer <token>`
 * for all subsequent API calls.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.username(), request.password());
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.username());
        // password check
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
