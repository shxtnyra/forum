package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.auth.AuthResponseDTO;
import com.shxtnyra.forum.dto.auth.LoginRequestDTO;
import com.shxtnyra.forum.dto.auth.RefreshTokenRequestDTO;
import com.shxtnyra.forum.dto.auth.RegisterRequestDTO;
import com.shxtnyra.forum.dto.confirmationToken.ConfirmationTokenDetailsDTO;
import com.shxtnyra.forum.dto.user.UserDetailsDTO;
import com.shxtnyra.forum.service.AuthService;
import com.shxtnyra.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDTO> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @GetMapping("/confirm")
    public ResponseEntity<ConfirmationTokenDetailsDTO> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.confirmToken(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.authenticate(dto.getLoginOrEmail(), dto.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request){
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {

        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
