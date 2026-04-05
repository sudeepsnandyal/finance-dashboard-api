package com.finance.dashboard.controller;

import com.finance.dashboard.model.dto.AuthRequest;
import com.finance.dashboard.model.dto.AuthResponse;
import com.finance.dashboard.model.dto.UserDTO;
import com.finance.dashboard.service.UserService;
import com.finance.dashboard.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<com.finance.dashboard.model.dto.ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Get full user details from database
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        return ResponseEntity.ok(com.finance.dashboard.model.dto.ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<com.finance.dashboard.model.dto.ApiResponse<UserDTO>> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);

        // Remove password from response
        createdUser.setPassword(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.finance.dashboard.model.dto.ApiResponse.success("User registered successfully", createdUser));
    }

    @GetMapping("/validate")
    public ResponseEntity<com.finance.dashboard.model.dto.ApiResponse<Boolean>> validateToken() {
        // If this endpoint is called, the token is valid (authentication would have failed otherwise)
        return ResponseEntity.ok(com.finance.dashboard.model.dto.ApiResponse.success("Token is valid", true));
    }
}
