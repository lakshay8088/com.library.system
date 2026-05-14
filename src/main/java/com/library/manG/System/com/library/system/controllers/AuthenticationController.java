package com.library.manG.System.com.library.system.controllers;

import com.library.manG.System.com.library.system.DTO.JwtResponse;
import com.library.manG.System.com.library.system.DTO.LoginRequest;
import com.library.manG.System.com.library.system.DTO.RegisterRequest;
import com.library.manG.System.com.library.system.DTO.RESPONSE;
import com.library.manG.System.com.library.system.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("User login attempt for username: {}", loginRequest.getUsername());
            JwtResponse jwtResponse = authenticationService.login(loginRequest);
            RESPONSE response = new RESPONSE("SUCCESS", jwtResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            RESPONSE response = new RESPONSE("FAILED", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("New user registration for username: {}", registerRequest.getUsername());
            JwtResponse jwtResponse = authenticationService.register(registerRequest);
            RESPONSE response = new RESPONSE("SUCCESS", jwtResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            RESPONSE response = new RESPONSE("FAILED", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            logger.info("Token refresh attempt");
            JwtResponse jwtResponse = authenticationService.refreshToken(token);
            RESPONSE response = new RESPONSE("SUCCESS", jwtResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            RESPONSE response = new RESPONSE("FAILED", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}

