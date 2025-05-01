package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u ORDER BY u.rating DESC LIMIT 50")
    List<UserEntity> findTop50ByOrderByRatingDesc();

    // Стандартные методы Spring Data JPA
    Page<UserEntity> findAll(Pageable pageable);

    // Поиск по подстроке в nickname с пагинацией
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nickname, '%'))")
    List<UserEntity> findByNicknameContainingIgnoreCase(@Param("nickname") String nickname);

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
