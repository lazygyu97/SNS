package com.sparta.sns.dto;

import com.sparta.sns.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String content;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post) {
        this.id=post.getId();
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.comments = post.getCommentList().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt).reversed()) // 작성날짜 내림차순
                .toList();
    }
}
