package com.sparta.sns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SignupAuth {
    @Id
    String email;

    @Column(nullable = false)
    private String authKey;

    @Column(nullable = false)
    private int authStatus;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    public SignupAuth(String email, String authKey) {
        this.email = email;
        this.authKey = authKey;
        this.authStatus = 0;
        this.expirationTime = LocalDateTime.now().plusMinutes(5);
    }

    public void changeStatusOK(){
        this.authStatus = 1;
    }
    public void changeStatusNO(){
        this.authStatus = 0;
    }
}
