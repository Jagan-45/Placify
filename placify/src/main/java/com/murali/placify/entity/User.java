package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;


import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userID;

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
    private List<Problem> problems;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Contest> createdContestList;

    @ManyToOne
    @JoinColumn(name = "dept_id",
            referencedColumnName = "dept_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "batch_id", referencedColumnName = "batch_id")
    private Batch batch;

    @ManyToMany
    @JoinTable(
            name = "contest_user",  // Join table name
            joinColumns = @JoinColumn(name = "user_id"),  // Foreign key in the join table for contest
            inverseJoinColumns = @JoinColumn(name = "contest_id")  // Foreign key
    )
    private List<Contest> assignedContests;
}
