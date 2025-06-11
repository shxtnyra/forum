package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.auth.RegisterRequestDTO;
import com.shxtnyra.forum.dto.confirmationToken.ConfirmationTokenDetailsDTO;
import com.shxtnyra.forum.dto.user.UserDetailsDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.entity.ConfirmationTokenEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.Role;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.ConfirmationTokenMapper;
import com.shxtnyra.forum.mapper.UserMapper;
import com.shxtnyra.forum.repository.ConfirmationTokenRepository;
import com.shxtnyra.forum.repository.UserRepository;
import com.shxtnyra.forum.service.interfaces.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;

    @Transactional
    public UserDetailsDTO createUser(RegisterRequestDTO request) {
        System.out.println("Попал в сервис createUser");

        // Если пользователь с таким именем уже есть, но с другой почтой
        userRepository.findByUsername(request.getUsername())
                .ifPresent(userEntity -> {
                    if (!userEntity.getEmail().equals(request.getEmail())) {
                        throw new IllegalArgumentException("Имя пользователя занято");
                    }
                });

        // Если уже есть аккаунт с такой почтой
        if (userRepository.existsByEmail(request.getEmail())) {
            UserEntity existingUser = userRepository.findByEmail(request.getEmail());

            // Если уже подтвержденный
            if (existingUser.isConfirmed()) {
                throw new IllegalArgumentException("Такая почта уже используется");
            }

            // TODO возможно нужно что-то с паролем делать
            // Если не подтвержден и вводимые данные совпадают, то отправляем повторно письмо
            if (existingUser.getUsername().equals(request.getUsername())) {
                // Предыдущие токены ставим недействительными
                confirmationTokenRepository.setExpired(LocalDateTime.now().minusHours(1), existingUser);

                emailSender.send(existingUser.getEmail(), EmailService.buildEmail(existingUser.getUsername(), createConfirmationToken(existingUser)));
                return UserMapper.toDetailsDTO(existingUser);
            }

            throw new IllegalArgumentException("Такая почта уже используется");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileDescription("")
                .role(Role.ROLE_USER)
                .totalRating(0)
                .weeklyRating(0)
                .build();

        user = userRepository.save(user);

        emailSender.send(request.getEmail(), EmailService.buildEmail(request.getUsername(), createConfirmationToken(user)));

        return UserMapper.toDetailsDTO(user);
    }

    // Создание токена
    public String createConfirmationToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        ConfirmationTokenEntity confirmationToken = ConfirmationTokenEntity.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // TODO: сделать из файла конфигурации время?
                .user(user)
                .build();
        confirmationTokenRepository.save(confirmationToken);

        return "http://localhost:8081/api/auth/confirm?token=" + token;
    }

    // Подтверждение токена активации
    @Transactional
    public ConfirmationTokenDetailsDTO confirmToken(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Токен не найден"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalArgumentException("Почта уже подтверждена");
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Токен истёк");
        }

        // Ставим токен активированным и активируем аккаунт
        confirmationTokenRepository.setConfirmed(token, LocalDateTime.now());
        userRepository.confirmUserEmail(confirmationToken.getUser().getEmail());

        return ConfirmationTokenMapper.toDetailsDTO(confirmationToken);
    }

    public UserDetailsDTO getUserById(Long id) {
        System.out.println("Попал в сервис getUserById");
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return UserMapper.toDetailsDTO(user);
    }

    // Оставлю пока для дебага
    public List<UserShortDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toShortDTO)
                .toList();
    }

    // для админов
    public Page<UserShortDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toShortDTO);
    }

    public List<UserShortDTO> getTopRatingUsers() {
        return userRepository.findTop50ByOrderByTotalRatingDesc()
                .stream()
                .map(UserMapper::toShortDTO)
                .toList();
    }

    // Поиск по нику, надо совместить с поиском по имени и где больше совпадение
    public List<UserShortDTO> findUsersByNickname(String nickname) {
        return userRepository.findByNicknameContainingIgnoreCase(nickname)
                .stream()
                .map(UserMapper::toShortDTO)
                .toList();
    }

    @Transactional
    public UserDetailsDTO updateUser(Long id, UserDetailsDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("Имя пользователя занято");
            }
            user.setNickname(dto.getNickname());
        }

        user.setName(dto.getName());
        user.setProfileDescription(dto.getProfileDescription());
        user.setAvatarUrl(dto.getAvatarURL());

        user = userRepository.save(user);
        return UserMapper.toDetailsDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
