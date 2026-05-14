package com.library.manG.System.com.library.system.Service;

import com.library.manG.System.com.library.system.DTO.JwtResponse;
import com.library.manG.System.com.library.system.DTO.LoginRequest;
import com.library.manG.System.com.library.system.DTO.RegisterRequest;
import com.library.manG.System.com.library.system.models.User;
import com.library.manG.System.com.library.system.repository.userRepository;
import com.library.manG.System.com.library.system.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    @Autowired
    private userRepository userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepo.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return new JwtResponse(accessToken, refreshToken, user.getId(), user.getUsername(), user.getEmail());
    }

    public JwtResponse register(RegisterRequest registerRequest) {
        if (userRepo.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        User savedUser = userRepo.save(user);

        String accessToken = jwtProvider.generateAccessToken(savedUser.getUsername(), savedUser.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(savedUser.getUsername());

        return new JwtResponse(accessToken, refreshToken, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getEmail());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return new JwtResponse(newAccessToken, newRefreshToken, user.getId(), user.getUsername(), user.getEmail());
    }
}

