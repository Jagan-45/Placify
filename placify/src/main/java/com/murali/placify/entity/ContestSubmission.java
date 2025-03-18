package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "contest_submission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id", "contest_id"})
)
/*
 * Stores latest accepted solution*/
public class ContestSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contestSubmissionID;

    @Column(name = "contest_submission_time", nullable = false)
    private float contestSubmissionTime; //in mins contest start time - submission time

    @ManyToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "problem_id",
            referencedColumnName = "problem_id")
    private Problem problem;
    @ManyToOne
    @JoinColumn(name = "contest_id",
            referencedColumnName = "contest_id")
    private Contest contest;


}
