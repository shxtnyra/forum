package com.shxtnyra.forum.entity;

import com.shxtnyra.forum.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Учетные данные для входа в систему:
     * - username: уникальный идентификатор для авторизации
     * - email: уникальная почта (альтернативный способ входа/восстановления)
     * - password: хэшированный пароль
     * -
     * P.S возможно стоит отказаться от username
     */
    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

// --- Публичные данные пользователя ---
    /**
     * "Настоящее" имя (не уникальное, может быть null).
     * Примеры с Хабра:
     * - "Арагорн" (ник @Lord_of_Rings)
     * - @Yukajii (тут имени нет, только ник)
     * - "Юра Туривный" (ник @turivny)
     */
    @Column(nullable = true, unique = false, length = 40)
    private String name;

    /**
     * Уникальный никнейм (например, @MatasDragonV).
     * Особенности:
     * - Не используется для входа (только login/email).
     * - Нельзя изменить после создания (immutable).
     * - Уникален в системе (аналог DTF/Habr/VK).
     * -
     * Контекст:
     * - В DTF: name не уникален, а nickname платный и меняет URL (@granger).
     * - В Steam: login для входа, name и nickname не уникальны (но nickname главнее).
     * - В VK: login для входа, name не уникален, nickname — бесплатный уникальный адрес.
     */
    @Column(nullable = true, unique = true, length = 32)
    private String nickname;

    @Column(nullable = false, length = 150)
    private String profileDescription;

    // Временная заглушка под фотографии
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "registration_date", updatable = false ,nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "rating", nullable = false)
    private int rating = 0;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    // Роли и права доступа
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_USER;  // ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN

    // Посты этого пользователя
    @OneToMany(mappedBy = "author", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostEntity> posts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
