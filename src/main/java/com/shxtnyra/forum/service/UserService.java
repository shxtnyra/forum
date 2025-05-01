package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.user.*;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.Role;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.UserMapper;
import com.shxtnyra.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileDTO createUser(UserRegisterDTO dto) {
        System.out.println("Попал в сервис createUser");

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profileDescription("")
                .role(Role.ROLE_USER)
                .rating(0)
                .build();

        user = userRepository.save(user);
        return UserMapper.toProfileDTO(user);
    }

    public UserProfileDTO getUserById(Long id) {
        System.out.println("Попал в сервис getUserById");
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return UserMapper.toProfileDTO(user);
    }

    // Оставлю пока для дебага
    public List<UserPreviewDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toPreviewDTO)
                .toList();
    }

    // для админов
    public Page<UserPreviewDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toPreviewDTO);
    }

    // Поиск по нику, надо совместить с поиском по имени и где больше совпадение
    public List<UserPreviewDTO> findUsersByNickname(String nickname) {
        return userRepository.findByNicknameContainingIgnoreCase(nickname)
                .stream()
                .map(UserMapper::toPreviewDTO)
                .toList();
    }

    public List<UserPreviewDTO> getTopRatingUsers() {
        return userRepository.findTop50ByOrderByRatingDesc().stream()
                .map(UserMapper::toPreviewDTO)
                .toList();
    }

    @Transactional
    public UserProfileDTO updateUser(Long id, UserProfileDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new RuntimeException();
                //throw new NicknameAlreadyExistsException("Nickname already in use");
            }
            user.setNickname(dto.getNickname());
        }

        user.setName(dto.getName());
        user.setProfileDescription(dto.getProfileDescription());
        user.setAvatarUrl(dto.getAvatarURL());

        user = userRepository.save(user);
        return UserMapper.toProfileDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


}