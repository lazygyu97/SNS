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
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;
    private Integer likeCount;
    private Boolean report;

    public PostResponseDto(Post post) {
        this.id=post.getId();
        this.content = post.getContent();
        this.imageUrl= post.getImageUrl();
        this.username = post.getUser().getUsername();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.report=post.isReport();
        this.comments = post.getCommentList().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt).reversed()) // 작성날짜 내림차순
                .toList();
        this.likeCount = post.getPostLikes().size();//배열로 넘어오는 좋아요 목록의 사이즈를 담아준다.

    }
}
