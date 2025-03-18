package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContestRepository extends JpaRepository<Contest, UUID> {
}
