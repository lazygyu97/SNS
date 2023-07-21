package com.sparta.sns.repository;

import com.sparta.sns.entity.Follow;
import com.sparta.sns.entity.PasswordManager;
import com.sparta.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    List<Follow> findByUser(User user);

    List<Follow> findByFollowingUser(User user);

    Optional<Follow> findByUserAndFollowingUser(User user, User followingUser);
}
