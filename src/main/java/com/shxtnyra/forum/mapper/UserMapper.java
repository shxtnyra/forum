package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.user.UserPreviewDTO;
import com.shxtnyra.forum.dto.user.UserProfileDTO;
import com.shxtnyra.forum.entity.UserEntity;

public class UserMapper {
    public static UserPreviewDTO toPreviewDTO(UserEntity user) {
        return UserPreviewDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .rating(user.getRating())
                .build();
    }

    public static UserProfileDTO toProfileDTO(UserEntity user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileDescription(user.getProfileDescription())
                .avatarURL(user.getAvatarUrl())
                .createAt(user.getRegistrationDate())
                .lastActivity(user.getLastActivity())
                .rating(user.getRating())
                .role(user.getRole())
                .build();
    }
}
