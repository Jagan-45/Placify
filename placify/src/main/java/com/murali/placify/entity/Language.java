package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "language_id")
    private int langID;

    @Column(name = "name",
    unique = true,
    nullable = false)
    private String name;

    @Column(name = "judge0_id",
    unique = true,
    nullable = false)
    private int judgeOID;
}
