package com.murali.placify.repository;

import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskScheduledRepo extends JpaRepository<TaskScheduled, UUID> {
    List<TaskScheduled> findAllByCreatedBy(User userById);
}
