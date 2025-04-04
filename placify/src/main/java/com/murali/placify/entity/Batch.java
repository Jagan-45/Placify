package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}