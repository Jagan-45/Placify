package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"tc_name", "problem_id"}))
public class Testcase {

    @Id
    @Column(name = "tc_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID testcase_id;

    private String tcName;

    @Column(name = "explanation", length = 500)
    private String explanation;

    private boolean sample;


    @ManyToOne
    @JoinColumn(name = "problem_id", referencedColumnName = "problem_id", updatable = false)
    @JsonIgnore
    private Problem problem;

}
