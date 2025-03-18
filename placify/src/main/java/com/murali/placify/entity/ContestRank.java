package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * contest will have the overall score and rank of users in particular contest
 * */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContestRank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contestRankId;
    @Column(name = "rank", nullable = false)
    private int rank;

    @ManyToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

}
