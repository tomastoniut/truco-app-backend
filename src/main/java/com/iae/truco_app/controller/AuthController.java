package com.iae.truco_app.controller;

import com.iae.truco_app.dto.LoginRequest;
import com.iae.truco_app.dto.LoginResponse;
import com.iae.truco_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        boolean authenticated = userService.authenticate(
            loginRequest.getUsername(), 
            loginRequest.getPassword()
        );
        
        if (authenticated) {
            return ResponseEntity.ok(
                new LoginResponse(true, "Login exitoso", loginRequest.getUsername())
            );
        } else {
            return ResponseEntity.status(401).body(
                new LoginResponse(false, "Usuario o contraseña incorrectos", null)
            );
        }
    }
}
