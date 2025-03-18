package com.murali.placify.entity;

import com.murali.placify.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
public class VerificationToken {

    public VerificationToken(){
        this.expireTime=calculationOfExpiryTime(EXPIRY_TIME);
    }

    private static final int EXPIRY_TIME = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String  token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime lastRequestTime;

    private LocalDateTime banStartTime;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    @OneToOne
    @JoinColumn(name = "user_id",
            referencedColumnName="user_id",
            nullable = false)
    private User user;

    private LocalDateTime calculationOfExpiryTime(int expiryTime) {
        return LocalDateTime.now().plusMinutes(expiryTime);
    }
}
