package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

//TODO: Define complete table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private LocalDate createdAt;

    private LocalDate assignedAt;

    private LocalDate completedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "assigned_to", referencedColumnName = "id")
    private User assignedTo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "assigned_by", referencedColumnName = "id")
    private User assignedBy;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemLink> problemLinks;

}
