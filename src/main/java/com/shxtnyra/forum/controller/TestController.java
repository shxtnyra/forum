package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.config.JwtConfig;
import com.shxtnyra.forum.dto.auth.AuthResponseDTO;
import com.shxtnyra.forum.dto.auth.LoginRequestDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.AuthService;
import com.shxtnyra.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtConfig jwtConfig;

    // Тестовые эндпоинты для проверки аутентификации

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Доступно всем, даже без аутентификации");
    }

    @GetMapping("/authenticated")
    public ResponseEntity<String> forAuthenticated(Principal principal) {
        return ResponseEntity.ok("Доступно любому аутентифицированному пользователю. Текущий: " + principal.getName());
    }

    @GetMapping("/user-role")
    public ResponseEntity<String> forUserRole(Principal user) {
        return ResponseEntity.ok("Только для USER. Ваша роль: " + user.getName());
    }

    @GetMapping("/moderator")
    public ResponseEntity<String> forModeratorRole(Authentication authentication) {
        return ResponseEntity.ok("getPrincipal: " + authentication.getPrincipal() + " \ngetName: "
         + authentication.getName() + " \ngetDetails: " + authentication.getDetails() + " \ngetCredentials:" +
                authentication.getCredentials() + " \ntoString:" + authentication.toString());
    }

    @GetMapping("/admin-role")
    public ResponseEntity<String> forAdminRole() {
        return ResponseEntity.ok("Только для ADMIN");
    }

    @GetMapping("/no-db-call")
    public ResponseEntity<String> noDBCall(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok("Данные из токена без запроса в БД. User: " + user.getUsername() + ", Role: " + user.getRole());
    }

    // Тестовые эндпоинты для регистрации/логина
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.authenticate(dto.getLoginOrEmail(), dto.getPassword()));
    }

    // Тестовые методы для проверки JWT
    @GetMapping("/check-token-validity")
    public ResponseEntity<String> checkTokenValidity(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Удаляем "Bearer "
        return ResponseEntity.ok("Токен валиден для пользователя: " +
                jwtConfig.getUsernameFromToken(token));
    }

    @GetMapping("/token-claims")
    public ResponseEntity<?> viewTokenClaims(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole(),
                "email", user.getEmail()
        ));
    }
}