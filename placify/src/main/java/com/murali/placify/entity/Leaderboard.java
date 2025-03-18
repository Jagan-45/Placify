package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.enums.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

//TODO: Define complete table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    private int overAllRating = 0;

    private int contestRating = 0;

    private int taskStreak = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Level level = Level.NEWBIE;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
