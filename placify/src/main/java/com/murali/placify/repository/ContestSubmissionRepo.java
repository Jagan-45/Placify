package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.ContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContestSubmissionRepo extends JpaRepository<ContestSubmission, UUID> {
    List<ContestSubmission> findAllByContest(Contest contest);

    @Query(value = "SELECT code FROM contest_submissions " +
            "WHERE contest_id = :contestId " +
            "AND problem_id = :problemId " +
            "AND user_id = :userId " +
            "AND status = 'ACCEPTED' " +
            "LIMIT 1", nativeQuery = true)
    String findFirstAccecptedCodeByUserContestProblem(@Param("contestId") UUID contestId, @Param("userId") UUID userId, @Param("problemId") UUID problemID);

}
