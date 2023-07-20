package com.sparta.sns.repository;

import com.sparta.sns.entity.PasswordManager;
import com.sparta.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PasswordManagerRepository extends JpaRepository<PasswordManager,Long> {

    Optional<PasswordManager> findByUser(User user);

    //DESC : 내림차순
    //LIMIT 3 : 3건만 조회
    @Query("SELECT pm.password FROM PasswordManager pm WHERE pm.user = :user ORDER BY pm.createdAt DESC LIMIT 3")
    List<String> findPasswordTopThree(@Param("user") User user);
}
