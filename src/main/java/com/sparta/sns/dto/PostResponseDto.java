package com.sparta.sns.dto;

import com.sparta.sns.entity.Post;

import java.time.LocalDateTime;


public class PostResponseDto {

    private String content;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post) {
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
