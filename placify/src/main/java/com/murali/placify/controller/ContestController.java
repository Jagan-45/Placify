package com.murali.placify.controller;

import com.murali.placify.model.ContestDto;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.ContestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/contest")
public class ContestController {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createContest(@Valid @RequestBody ContestDto contestDto) {
        System.out.println(contestDto);
        contestService.CreateContest(contestDto);
        return new ResponseEntity<>(new ApiResponse("contest created successfully", null), HttpStatus.OK);
    }
}
