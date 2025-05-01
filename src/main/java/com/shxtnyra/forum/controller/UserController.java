package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.user.UserPreviewDTO;
import com.shxtnyra.forum.dto.user.UserProfileDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.exception.exceptions.ValidationException;
import com.shxtnyra.forum.mapper.UserMapper;
import com.shxtnyra.forum.service.AuthService;
import com.shxtnyra.forum.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с пользователями системы.
 * Предоставляет API для управления пользовательскими профилями и их поиска.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    /**
     * Получить список всех пользователей в виде List (без пагинации).
     * Используется для выпадающих списков и других сценариев, где нужен полный перечень.
     *
     * @return List<UserPreviewDTO> список пользователей в сокращенном формате
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserPreviewDTO>> getAllUsersList() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Получить страницу пользователей с пагинацией.
     *
     * @return Page<UserPreviewDTO> страница пользователей с метаданными пагинации
     * @apiNote Размер страницы по умолчанию: 10 элементов. Для изменения параметров
     *          используйте query-параметры: ?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<UserPreviewDTO>> getAllUsersPage(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * Получить профиль пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return UserProfileDTO полные данные профиля
     * @throws EntityNotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Поиск пользователей по никнейму (регистронезависимый).
     *
     * @param nickname часть никнейма для поиска
     * @return List<UserPreviewDTO> список подходящих пользователей
     * @apiNote Минимальная длина запроса: 3 символа. Максимальное количество результатов: 20.
     */
    @GetMapping("/find")
    public ResponseEntity<List<UserPreviewDTO>> findUserByNickname(
            @RequestParam @Size(min = 3, max = 32) String nickname) {
        return ResponseEntity.ok(userService.findUsersByNickname(nickname));
    }

    /**
     * Получить профиль текущего аутентифицированного пользователя.
     *
     * @param currentUser автоматически внедряемый объект пользователя из контекста безопасности
     * @return UserProfileDTO полные данные профиля
     * @throws AuthenticationException если пользователь не аутентифицирован
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(UserMapper.toProfileDTO(currentUser));
    }

    /**
     * Обновить профиль текущего пользователя.
     *
     * @param updateDto DTO с обновляемыми полями
     * @param currentUser аутентифицированный пользователь
     * @return UserProfileDTO обновленный профиль
     * @throws ValidationException при невалидных данных
     * @implNote Поддерживает частичное обновление через PATCH.
     *           Нельзя изменить email и username после регистрации.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            @Valid @RequestBody UserProfileDTO updateDto,
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(
                userService.updateUser(currentUser.getId(), updateDto)
        );
    }

    /**
     * Удалить аккаунт текущего пользователя.
     *
     * @param currentUser аутентифицированный пользователь
     * @return HTTP 204 No Content при успешном удалении
     * @implNote Выполняет "мягкое" удаление (помечает запись как неактивную).
     *           Для полного удаления требуется роль ADMIN.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal UserEntity currentUser) {
        authService.deleteRefreshTokenByUser(currentUser.getId());
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
