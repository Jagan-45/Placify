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
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "submission_id")
    private UUID submissionID;
    @Column(name = "code", length = 4500, nullable = false)
    private String code;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "runtime")
    private float runtime;
    @Column(name = "submission_time", nullable = false)
    private LocalDateTime submissionTime;

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
            referencedColumnName = "contest_id")
    private Contest contest;
}
