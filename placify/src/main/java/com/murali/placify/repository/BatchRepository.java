package com.murali.placify.repository;

import com.murali.placify.entity.Batch;
import com.murali.placify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

    List<Batch> findAllByBatchNameIn(List<String> names);

    List<Batch> findAllByCreatedBy(User user);
}
