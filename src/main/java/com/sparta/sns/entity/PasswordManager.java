package com.sparta.sns.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "passwordManager")
public class PasswordManager{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PasswordManager(String password, User user) {
        this.password = password;
        this.user=user;
    }
}
