package com.sparta.sns.entity;

import com.sparta.sns.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 내용
    @Column(nullable = false)
    private String content;

    // 외래키로 post_id 받아오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 외래키로 user_id 받아오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(PostRequestDto requestDto,User user, Post post) {
       this.content = requestDto.getContent();
       this.user = user;
       this.post = post;
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
