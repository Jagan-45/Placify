package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(
        name = "user_boiler_plate",
        uniqueConstraints = @UniqueConstraint(columnNames = {"problem_id", "language_id"})
)
public class UserBoilerPlate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userBpID;

    @Column(name = "boiler_plate", length = 1000)
    private String boilerPlate;

    @ManyToOne
    @JoinColumn(name = "problem_id",
            referencedColumnName = "problem_id",
            nullable = false)
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "language_id",
            referencedColumnName = "language_id",
            nullable = false)
    private Language language;
}
