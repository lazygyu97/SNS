package com.sparta.sns.repository;

import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByUserIdOrderByCreatedAtDesc(Long id);

    List<Post> findByUsernameOrderByCreatedAtDesc(String username);

    List<Post> findByReportOrderByCreatedAtDesc(boolean b);
}
