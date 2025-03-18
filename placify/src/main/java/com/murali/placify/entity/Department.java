package com.murali.placify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    @Id

    @Column(name = "dept_id")
    private int deptID;

    @Column(name = "dept_name",
            nullable = false)
    private String deptName;

}
