package com.murali.placify.repository;

import com.murali.placify.entity.ProblemLink;
import com.murali.placify.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemLinkRepo extends JpaRepository<ProblemLink, UUID> {
    List<ProblemLink> findAllByTask(Task task);
}
