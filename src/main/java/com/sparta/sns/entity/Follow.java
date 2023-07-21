package com.sparta.sns.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    // 외래키로 user_id 받아오기
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 나

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", nullable = false)
    private User followingUser; // 내가 팔로우 하는 사용자 나 -> 사용자.

    //팔로잉 한 일시
    @Column
    private LocalDateTime followingDateTime;

    public Follow(User user, User followingUser) {
        this.user = user;
        this.followingUser = followingUser;
        this.followingDateTime = LocalDateTime.now();
    }
}
