package com.sparta.sns.repository;

import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.PostLike;
import com.sparta.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
}