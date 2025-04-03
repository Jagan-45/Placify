package com.murali.placify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.Mapper.UserScoreDto;
import com.murali.placify.model.*;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.ContestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping("/submit-code")
    public ResponseEntity<ApiResponse> submitCode(@RequestBody ContestSubmissionDto problemSubmissionDto) throws JsonProcessingException {
        List<SubmissionResult> submissionResult = contestService.submitContestProblem(UUID.fromString("f9d89387-0c21-4b7b-b7ff-c922debfd56f"), problemSubmissionDto);
        return new ResponseEntity<>(new ApiResponse("", submissionResult), HttpStatus.OK);
    }

    @PostMapping("/enter-contest/{contestId}")
    public ResponseEntity<ApiResponse> enterContest(@PathVariable("contestId") UUID contestId) {
        ContestResponseDto dto = contestService.enterContest(UUID.fromString("f9d89387-0c21-4b7b-b7ff-c922debfd56f"), contestId);
        return new ResponseEntity<>(new ApiResponse("You have entered the contest, please do not leave the contest screen", dto), HttpStatus.OK);
    }

    @PostMapping("/exit-contest/{contestId}")
    public ResponseEntity<ApiResponse> exitContest(@PathVariable("contestId") UUID contestId) {
        contestService.exitContest(UUID.fromString("0445db66-c716-4d9e-9ce0-c84d83fd679c"), contestId);
        return new ResponseEntity<>(new ApiResponse("You have exited the contest", null), HttpStatus.OK);
    }

    @PostMapping("/submit-contest/{contestId}")
    public ResponseEntity<ApiResponse> submitContest(@PathVariable("contestId") UUID contestId) {
        contestService.submitContest(UUID.fromString("0445db66-c716-4d9e-9ce0-c84d83fd679c"), contestId);
        return new ResponseEntity<>(new ApiResponse("You have submitted the contest", null), HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse> updateContest(@RequestBody ContestUpdateDto dto) {
        contestService.updateScheduledContest(UUID.fromString("50bb1bbd-03df-418b-bf5f-fbff750dde9f"), dto);
        return new ResponseEntity<>(new ApiResponse("contest schedule updated", null), HttpStatus.OK);
    }

    @DeleteMapping("/{contestId}")
    public ResponseEntity<ApiResponse> deleteContest(@PathVariable("contestId") UUID id) {
        contestService.deleteScheduledContest(UUID.fromString("50bb1bbd-03df-418b-bf5f-fbff750dde9f"), id);
        return new ResponseEntity<>(new ApiResponse("contest schedule deleted", null), HttpStatus.OK);
    }

    @GetMapping("/leaderboard/{id}")
    public ResponseEntity<ApiResponse> getContestLeaderboard(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(new ApiResponse("", contestService.getLeaderboard(id)), HttpStatus.OK);
    }

}
