package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.auth.AuthResponse;
import com.shxtnyra.forum.dto.auth.RefreshTokenRequest;
import com.shxtnyra.forum.dto.user.UserLoginDTO;
import com.shxtnyra.forum.dto.user.UserProfileDTO;
import com.shxtnyra.forum.dto.user.UserRegisterDTO;
import com.shxtnyra.forum.service.AuthService;
import com.shxtnyra.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileDTO> register(@RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authService.authenticate(dto.getLoginOrEmail(), dto.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
