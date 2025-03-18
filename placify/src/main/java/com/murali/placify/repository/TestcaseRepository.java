package com.murali.placify.repository;

import com.murali.placify.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, UUID> {

    @Query("""
            select t from Testcase t where t.problem.problemSlug = :problemSlug and t.sample = true
            """)
    List<Testcase> findSampleByProblemSlug(String problemSlug);

    Optional<List<Testcase>> findAllByProblemProblemSlug(String problemSlug);
}
