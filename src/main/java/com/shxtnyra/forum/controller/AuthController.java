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
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    /**
     * Регистрация нового пользователя.
     *
     * @param dto DTO с регистрационными данными (username, email, password и др.)
     * @return UserDetailsDTO данные созданного пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<UserDetailsDTO> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    /**
     * Подтверждение email пользователя по токену.
     *
     * @param token токен подтверждения, отправленный на email
     * @return ConfirmationTokenDetailsDTO информация о подтверждении
     */
    @GetMapping("/confirm")
    public ResponseEntity<ConfirmationTokenDetailsDTO> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.confirmToken(token));
    }

    /**
     * Аутентификация пользователя (логин).
     *
     * @param dto DTO с логином/email и паролем
     * @return AuthResponseDTO access и refresh токены
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.authenticate(dto.getLoginOrEmail(), dto.getPassword()));
    }

    /**
     * Обновление access токена по refresh токена.
     *
     * @param request DTO с refresh токеном
     * @return AuthResponseDTO новые access и refresh токены
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request){
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    /**
     * Выход пользователя (инвалидация refresh токена).
     *
     * @param request DTO с refresh токеном
     * @return HTTP 204 No Content при успешном выходе
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
