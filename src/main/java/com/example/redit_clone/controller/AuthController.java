package com.example.redit_clone.controller;

import com.example.redit_clone.dto.AuthenticationResponse;
import com.example.redit_clone.dto.LoginRequest;
import com.example.redit_clone.dto.RegisterRequest;
import com.example.redit_clone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest registerRequest) {
        authService.signUp(registerRequest);
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @GetMapping("accountVerification")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
        //we need to make mail sending functionality asynchrnously
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {

        return authService.login(loginRequest);
    }
}
