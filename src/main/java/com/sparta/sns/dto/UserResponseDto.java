package com.sparta.sns.dto;

import com.sparta.sns.entity.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {
    private String username;
    private String email;
    private UserRoleEnum role;
    private String nickname;
    private LocalDateTime createdAt;


}
