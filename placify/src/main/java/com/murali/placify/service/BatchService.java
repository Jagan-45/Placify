package com.murali.placify.service;

import com.murali.placify.entity.Batch;
import com.murali.placify.repository.BatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {
    private final BatchRepository batchRepository;


    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<Batch> findBatchesByName(List<String> names) {
        return batchRepository.findAllByBatchNameIn(names);
    }

    public List<Batch> saveAll(List<Batch> batches){
        return batchRepository.saveAll(batches);
    }
}
