package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.auth.RegisterRequestDTO;
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
    public UserDetailsDTO createUser(RegisterRequestDTO request) {
        System.out.println("Попал в сервис createUser");

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
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
        return UserMapper.toDetailsDTO(user);
    }

    public UserDetailsDTO getUserById(Long id) {
        System.out.println("Попал в сервис getUserById");
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
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

    public List<UserShortDTO> getTopRatingUsers(){
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("Username already exists");
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
