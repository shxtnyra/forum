package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.dto.user.UserDetailsDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.exception.exceptions.ValidationException;
import com.shxtnyra.forum.mapper.UserMapper;
import com.shxtnyra.forum.service.AuthService;
import com.shxtnyra.forum.service.CommentService;
import com.shxtnyra.forum.service.UserRatingService;
import com.shxtnyra.forum.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
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
@RequestMapping("v1/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final CommentService commentService;

    /**
     * Получить список всех пользователей в виде List (без пагинации).
     * Используется для выпадающих списков и других сценариев, где нужен полный перечень.
     *
     * @return List<UserShortDTO> список пользователей в сокращенном формате
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserShortDTO>> getAllUsersList() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Получить страницу пользователей с пагинацией.
     *
     * @return Page<UserShortDTO> страница пользователей с метаданными пагинации
     * @apiNote Размер страницы по умолчанию: 10 элементов. Для изменения параметров
     *          используйте query-параметры: ?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<UserShortDTO>> getAllUsersPage(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/top")
    public ResponseEntity<List<UserShortDTO>> getTopRatingUsers(){
        return ResponseEntity.ok(userService.getTopRatingUsers());
    }

    /**
     * Получить профиль пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return UserDetailsDTO полные данные профиля
     * @throws EntityNotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<Page<CommentShortDTO>> getCommentsByUser(@PathVariable Long id,
                                                                   @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(id, pageable));
    }

    /**
     * Поиск пользователей по никнейму (регистронезависимый).
     *
     * @param nickname часть никнейма для поиска
     * @return List<UserShortDTO> список подходящих пользователей
     * @apiNote Минимальная длина запроса: 3 символа. Максимальное количество результатов: 20.
     */
    @GetMapping("/find")
    public ResponseEntity<List<UserShortDTO>> findUserByNickname(
            @RequestParam @Size(min = 3, max = 32) String nickname) {
        return ResponseEntity.ok(userService.findUsersByNickname(nickname));
    }

    /**
     * Получить профиль текущего аутентифицированного пользователя.
     *
     * @param currentUser автоматически внедряемый объект пользователя из контекста безопасности
     * @return UserDetailsDTO полные данные профиля
     * @throws AuthenticationException если пользователь не аутентифицирован
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailsDTO> getMyProfile(
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(UserMapper.toDetailsDTO(currentUser));
    }

    /**
     * Обновить профиль текущего пользователя.
     *
     * @param updateDto DTO с обновляемыми полями
     * @param currentUser аутентифицированный пользователь
     * @return UserDetailsDTO обновленный профиль
     * @throws ValidationException при невалидных данных
     * @implNote Поддерживает частичное обновление через PATCH.
     *           Нельзя изменить email и username после регистрации.
     */
    @PutMapping("/me")
    public ResponseEntity<UserDetailsDTO> updateMyProfile(
            @Valid @RequestBody UserDetailsDTO updateDto,
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
