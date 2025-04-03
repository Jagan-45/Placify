package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "problems")
public class Problem {

    @Id
    @Column(name = "problem_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID problemID;

    @Column(name = "problem_name", nullable = false)
    private String problemName;

    @Column(name = "problem_slug", nullable = false)
    private String problemSlug;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "correct_code", length = 3500)
    @JsonIgnore
    private String correctCode;

    @Column(name = "points", nullable = false)
    private int points;

    @OneToMany(mappedBy = "problem", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Testcase> testcases;

    @Column(name = "constrains")
    private String constrains;

    @Column(name = "input_fields")
    private String inputFields;

    @Column(name = "output_field")
    private String outputField;

    @JsonIgnore
    private boolean visible;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by",
            referencedColumnName = "user_id",
            nullable = false)
    private User createdBy;

    @ManyToMany(mappedBy = "problemList")
    @JsonIgnore
    private Set<Contest> contestSet;

}
