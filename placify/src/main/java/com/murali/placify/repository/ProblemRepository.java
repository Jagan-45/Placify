package com.murali.placify.repository;

import com.murali.placify.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.murali.placify.entity.Problem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {


    boolean existsByProblemSlug(@NotBlank(message = "invalid folder name") String problemSlug);

    Optional<Problem> findByProblemSlug(String problemSlug);

    Optional<List<Problem>> findByCreatedBy(User user);
}
