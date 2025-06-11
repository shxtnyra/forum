package com.shxtnyra.forum.service;

import com.shxtnyra.forum.config.JwtConfig;
import com.shxtnyra.forum.dto.auth.AuthResponseDTO;
import com.shxtnyra.forum.entity.RefreshTokenEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtConfig jwtConfig;

    @Transactional
    public AuthResponseDTO authenticate(String usernameOrEmail, String password) {
//        UserEntity user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
//                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("Invalid credentials");
//        }

        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
                );

        String accessToken = jwtConfig.generateToken((UserEntity) authentication.getPrincipal());
        RefreshTokenEntity refreshToken = createRefreshToken((UserEntity) authentication.getPrincipal());

        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken){
        RefreshTokenEntity oldRefreshToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Refresh токен не найден: " + refreshToken));

        verifyExpirationRefreshToken(oldRefreshToken);

        String accessToken = jwtConfig.generateToken(oldRefreshToken.getUser());
        RefreshTokenEntity newRefreshToken = createRefreshToken(oldRefreshToken.getUser());

        return new AuthResponseDTO(accessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByUser(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }

    private RefreshTokenEntity createRefreshToken(UserEntity user){
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(jwtConfig.generateRefreshToken(user))
                .expiryDate(Instant.now().plusMillis(jwtConfig.getRefreshExpiration()))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private void verifyExpirationRefreshToken(RefreshTokenEntity refreshToken) {
        if (Instant.now().isAfter(refreshToken.getExpiryDate())) {
            refreshTokenRepository.delete(refreshToken);
            throw new JwtException("Refresh токен истёк");
        }
    }

}
