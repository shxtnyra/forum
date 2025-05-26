package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.user.UserDetailsDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.entity.UserEntity;

public class UserMapper {
    public static UserShortDTO toShortDTO(UserEntity user) {
        return UserShortDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .totalRating(user.getTotalRating())
                .build();
    }

    public static UserDetailsDTO toDetailsDTO(UserEntity user) {
        return UserDetailsDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileDescription(user.getProfileDescription())
                .avatarURL(user.getAvatarUrl())
                .createAt(user.getRegistrationDate())
                .lastActivity(user.getLastActivity())
                .totalRating(user.getTotalRating())
                .role(user.getRole())
                .isConfirmed(user.isConfirmed())
                .build();
    }
}
