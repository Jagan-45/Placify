package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;

    private String token;

    private LocalDateTime refreshTokenExpiryTime;

    private boolean loggedIn;

    @OneToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

    public RefreshToken(User user, String jwt, LocalDateTime now, boolean loggedIn) {
        this.user = user;
        this.token = jwt;
        this.refreshTokenExpiryTime = now;
        this.loggedIn = loggedIn;
    }
}