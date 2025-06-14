package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.ConfirmationTokenEntity;
import com.shxtnyra.forum.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, Long> {
    // Поиск по токену
    Optional<ConfirmationTokenEntity> findByToken(String token);

    // Установка токена подтвержденным
    @Modifying
    @Query("UPDATE ConfirmationTokenEntity c SET c.confirmedAt = ?2 WHERE c.token = ?1")
    void setConfirmed(String token, LocalDateTime confirmedAt);

    // Установка токена недействительным
    @Modifying
    @Query("UPDATE ConfirmationTokenEntity c SET c.expiresAt = ?1 WHERE c.user =?2")
    void setExpired(LocalDateTime expiredAt, UserEntity user);
}
