package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.User;
import com.murali.placify.enums.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContestRepository extends JpaRepository<Contest, UUID> {
    boolean existsByContestIDAndUserAssignedTo_UserID(UUID contestId, UUID userId);

    List<Contest> findAllByStatus(ContestStatus contestStatus);

    boolean existsByCreatedByAndContestID(User createdBy, UUID contestId);
}
