package com.murali.placify.repository;

import com.murali.placify.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Byte> {
}
