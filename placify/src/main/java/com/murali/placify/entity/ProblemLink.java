package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem_links")
public class ProblemLink {

    public ProblemLink(String s, Task t) {
        link = s;
        task = t;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String link;

    private boolean solved;

    private boolean attempted;

    private double acRate;

    private String difficulty;

    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @JsonIgnore
    private Task task;

}
