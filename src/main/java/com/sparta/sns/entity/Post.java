package com.sparta.sns.entity;

import com.sparta.sns.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 작성한 글 내용
    @Column(nullable = false)
    private String content; // SNS니까 글이 길지 않아서 String으로 선언해둠!

    // 게시글 신고 여부 플래그
    @Column
    private boolean repoort_flag = false;

    // 외래키로 user_id 받아오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 외래키로 댓글 받아오기
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true) // 삭제, 업데이트 시 반영됨
    private List<Comment> commentList = new ArrayList<>();

    public Post(PostRequestDto requestDto, User user) {
        this.content = requestDto.getContent();
        this.user = user;
    }

    public void update(PostRequestDto requestDto) {
        this.content =  requestDto.getContent();
    }
}
