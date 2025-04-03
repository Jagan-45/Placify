package com.murali.placify.repository;

import com.murali.placify.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

    List<Batch> findAllByBatchNameIn(List<String> names);
}
