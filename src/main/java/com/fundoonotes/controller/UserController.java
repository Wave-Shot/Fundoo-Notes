package com.fundoonotes.controller;

import com.fundoonotes.dto.request.*;
import com.fundoonotes.dto.response.*;
import com.fundoonotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getProfile(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Strip "Bearer " prefix if present
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        userService.logout(rawToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}