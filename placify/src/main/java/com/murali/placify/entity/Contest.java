package com.murali.placify.entity;

import com.murali.placify.enums.ContestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contest_id")
    private UUID contestID;
    @Column(name = "contest_name", nullable = false)
    private String contestName;
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    private ContestStatus status;
    @ManyToOne
    @JoinColumn(name = "created_by",
            referencedColumnName = "user_id")
    private User createdBy;

    @ManyToMany(mappedBy = "assignedContests")
    private List<User> userAssignedTo;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "contest_problem",  // Join table name
            joinColumns = @JoinColumn(name = "contest_id"),  // Foreign key in the join table for contest
            inverseJoinColumns = @JoinColumn(name = "problem_id")  // Foreign key in the join table for problem
    )
    private List<Problem> problemList;
}
