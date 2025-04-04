package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.core.annotation.Order;


import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userID;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    @JsonIgnore
    private String mailID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled;

    @Column(nullable = true)
    private int year;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Problem> problems;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Contest> createdContestList;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> assignedTasks;

    @ManyToOne
    @JoinColumn(name = "dept_id",
            referencedColumnName = "dept_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "batch_id", referencedColumnName = "batch_id")
    private Batch batch;

    @ManyToMany(mappedBy = "userAssignedTo")
    @JsonIgnore
    private List<Contest> assignedContests;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Leaderboard leaderboard;
}
