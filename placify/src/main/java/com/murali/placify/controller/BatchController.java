package com.murali.placify.controller;

import com.murali.placify.model.BatchResDto;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.BatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v0/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createBatch(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("batchName") String batchName) {
        List<BatchResDto> result = batchService.createBatch(file, batchName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("batch name", batchName);
        resultMap.put("data", result);

        return new ResponseEntity<ApiResponse>(new ApiResponse("Batch created successfully", resultMap), HttpStatus.OK);

    }
}
