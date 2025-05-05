package com.shxtnyra.forum.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDTO {
    private String username;

    private String email;

    private String password; // Будет хэшироваться перед сохранением

    private String name;

    private String profileDescription;

    private String avatarUrl;
}
