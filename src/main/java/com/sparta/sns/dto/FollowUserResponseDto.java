package com.sparta.sns.dto;

import com.sparta.sns.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class FollowUserResponseDto {
    private String username;
    private String email;
    private String nickname;
    private String image;
    private LocalDateTime followingDateTime;
    public FollowUserResponseDto(User followingUser, LocalDateTime followingDateTime) {
        this.username = followingUser.getUsername();
        this.email = followingUser.getEmail();
        this.image = followingUser.getImage();
        this.nickname = followingUser.getNickname();
        this.followingDateTime = followingDateTime;
    }
}

