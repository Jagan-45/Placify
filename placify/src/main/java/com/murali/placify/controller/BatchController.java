package com.murali.placify.controller;

import com.murali.placify.entity.User;
import com.murali.placify.model.BatchResDto;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.BatchService;
import com.murali.placify.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/batches")
public class BatchController {

    private final UserService userService;
    private final BatchService batchService;

    public BatchController(UserService userService, BatchService batchService) {
        this.userService = userService;
        this.batchService = batchService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createBatch(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("batchName") String batchName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        List<BatchResDto> result = batchService.createBatch(userId, file, batchName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("batch name", batchName);
        resultMap.put("data", result);

        return new ResponseEntity<ApiResponse>(new ApiResponse("Batch created successfully", resultMap), HttpStatus.OK);

    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getBatches() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        return new ResponseEntity<>(new ApiResponse("", batchService.getAllBatchesCreatedBy(userId)), HttpStatus.OK);
    }
}
