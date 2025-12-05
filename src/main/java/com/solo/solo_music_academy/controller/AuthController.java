package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.*;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.UserRepository;
import com.solo.solo_music_academy.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173") // 👈 cho phép FE
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    try {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        String token = jwtService.generateToken(req.getUsername());

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(name -> name.startsWith("ROLE_"))   // ⭐⭐
                .collect(Collectors.toSet());

        LoginResponse res = LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(roles)
                .build();

        return ResponseEntity.ok(res);

    } catch (BadCredentialsException ex) {
        return ResponseEntity.status(401).body("Sai username hoặc password");
    }
}
}
