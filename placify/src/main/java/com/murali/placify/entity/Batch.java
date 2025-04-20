package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private int batchID;

    @Column(nullable = false, unique = true)
    private String batchName;

    @ManyToMany(mappedBy = "batches")
    @JsonIgnore
    private List<TaskScheduled> tasksScheduled;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    @JsonIgnore
    private User createdBy;

    @OneToMany(mappedBy = "batch")
    @JsonIgnore
    private List<User> students = new ArrayList<>();

    // To avoid null pointers on entities created before adding empty list on variable
    @PostLoad
    public void init() {
        if (students == null) {
            students = new ArrayList<>();
        }
    }
}