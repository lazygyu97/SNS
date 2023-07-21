package com.sparta.sns.dto;

import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
public class FollowUserResponseDto {
    private String username;
    private String email;
    private UserRoleEnum role;
    private String nickname;
    private LocalDateTime followingDateTime;
    public FollowUserResponseDto(User followingUser, LocalDateTime followingDateTime) {
        this.username = followingUser.getUsername();
        this.email = followingUser.getEmail();
        this.role = followingUser.getRole();
        this.nickname = followingUser.getNickname();
        this.followingDateTime = followingDateTime;
    }
}

