package com.murali.placify.repository;

import com.murali.placify.entity.Task;
import com.murali.placify.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID> {
    Optional<Task> findByAssignedAtAndAssignedTo(LocalDate date, User userById);

    @Modifying
    @Transactional
    @Query("UPDATE ProblemLink p SET p.solved = true " +
            "WHERE p.id = :problemId " +
            "AND p.task.id = :taskId " +
            "AND p.task.assignedTo.id = :userId")
    int markProblemAsSolved(@Param("userId") UUID userId,
                            @Param("taskId") UUID taskId,
                            @Param("problemId") UUID problemId);
}
