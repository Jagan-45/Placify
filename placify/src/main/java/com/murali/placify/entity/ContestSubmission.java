package com.murali.placify.entity;

import com.murali.placify.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contest_submissions")
/*
 * Stores all accepted solution*/
public class ContestSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contest_submission_id")
    private UUID contestSubmissionID;

    @Column(name = "contest_submission_time", nullable = false)
    private float contestSubmissionTime; //in mins contest start time - submission time

    @ManyToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "problem_id",
            referencedColumnName = "problem_id",
            nullable = false)
    private Problem problem;
    @ManyToOne
    @JoinColumn(name = "contest_id",
            referencedColumnName = "contest_id",
            nullable = false)
    private Contest contest;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

}
