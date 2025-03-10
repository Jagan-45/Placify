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
public class ProblemLink {

    public ProblemLink(String s, Task t) {
        link = s;
        task = t;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String link;

    private boolean solved;

    private boolean attempted;

    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @JsonIgnore
    private Task task;

}
