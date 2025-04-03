package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.ContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContestSubmissionRepo extends JpaRepository<ContestSubmission, UUID> {
    List<ContestSubmission> findAllByContest(Contest contest);
}
