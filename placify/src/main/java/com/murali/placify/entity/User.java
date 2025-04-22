package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.core.annotation.Order;


import java.util.ArrayList;
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
    @JsonIgnore
    private Role role;

    @JsonIgnore
    private boolean enabled;

    @Column(nullable = true)
    private int year;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Problem> problems = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Contest> createdContestList = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> assignedTasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "dept_id",
            referencedColumnName = "dept_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "batch_id", referencedColumnName = "batch_id")
    private Batch batch;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ContestUser> contestAssignedTo = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Leaderboard leaderboard;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<TaskScheduled> scheduledTasks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Batch> batchesCreated = new ArrayList<>();

    //To avoid null pointers on entities created before adding empty list on variable
    @PostLoad
    public void init() {
        if (problems == null) problems = new ArrayList<>();
        if (createdContestList == null) createdContestList = new ArrayList<>();
        if (assignedTasks == null) assignedTasks = new ArrayList<>();
        if (contestAssignedTo == null) contestAssignedTo = new ArrayList<>();
        if (scheduledTasks == null) scheduledTasks = new ArrayList<>();
        if (batchesCreated == null) batchesCreated = new ArrayList<>();
    }
}
