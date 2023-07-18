package com.sparta.sns.repository;

import com.sparta.sns.entity.SignupAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SignupAuthRepository extends JpaRepository<SignupAuth, Long> {
    Optional<SignupAuth> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM SignupAuth WHERE expirationTime < NOW()")
    void deleteExpiredSignupAuth();
}
