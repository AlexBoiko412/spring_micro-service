package com.p.authservice.controller;


import com.p.authservice.dto.LoginRequestDTO;
import com.p.authservice.dto.LoginResponseDTO;
import com.p.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {
      private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    @Operation(summary = "User login and token generation")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDto) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDto);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT Token")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        if(authHeader == null || authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
