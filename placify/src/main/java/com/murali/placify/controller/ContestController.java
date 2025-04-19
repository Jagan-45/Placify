package com.murali.placify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.Mapper.UserScoreDto;
import com.murali.placify.model.*;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.ContestService;
import com.murali.placify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/contest")
public class ContestController {
    private final ContestService contestService;
    private final UserService userService;

    public ContestController(ContestService contestService, UserService userService) {
        this.contestService = contestService;
        this.userService = userService;
    }

//    @PreAuthorize("hasRole('ROLE_STAFF')")
//    @PostMapping()
//    public ResponseEntity<ApiResponse> createContest(@Valid @RequestBody ContestDto contestDto) {
//        System.out.println(contestDto);
//        contestService.createContest(contestDto);
//        return new ResponseEntity<>(new ApiResponse("contest created successfully", null), HttpStatus.OK);
//    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PostMapping()
    public ResponseEntity<ApiResponse> createContest(@Valid @RequestBody CreateContestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        contestService.createContestAutomated(userId, dto);
        return new ResponseEntity<>(new ApiResponse("contest created successfully", null), HttpStatus.OK);
    }

    @PostMapping("/submit-code")
    public ResponseEntity<ApiResponse> submitCode(@RequestBody ContestSubmissionDto problemSubmissionDto) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        List<SubmissionResult> submissionResult = contestService.submitContestProblem(userId, problemSubmissionDto);
        return new ResponseEntity<>(new ApiResponse("", submissionResult), HttpStatus.OK);
    }

    @PostMapping("/enter-contest/{contestId}")
    public ResponseEntity<ApiResponse> enterContest(@PathVariable("contestId") UUID contestId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        Map<String, Object> dto = contestService.enterContest(userId, contestId);

        return new ResponseEntity<>(new ApiResponse("You have entered the contest, please do not leave the contest screen", dto), HttpStatus.OK);
    }

    @PostMapping("/exit-contest/{contestId}")
    public ResponseEntity<ApiResponse> exitContest(@PathVariable("contestId") UUID contestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        contestService.exitContest(userId, contestId);
        return new ResponseEntity<>(new ApiResponse("You have exited the contest", null), HttpStatus.OK);
    }

    @PostMapping("/submit-contest/{contestId}")
    public ResponseEntity<ApiResponse> submitContest(@PathVariable("contestId") UUID contestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        contestService.submitContest(userId, contestId);
        return new ResponseEntity<>(new ApiResponse("You have submitted the contest", null), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PutMapping()
    public ResponseEntity<ApiResponse> updateContest(@RequestBody ContestUpdateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        contestService.updateScheduledContest(userId, dto);
        return new ResponseEntity<>(new ApiResponse("contest schedule updated", null), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @DeleteMapping("/{contestId}")
    public ResponseEntity<ApiResponse> deleteContest(@PathVariable("contestId") UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        contestService.deleteScheduledContest(userId, id);
        return new ResponseEntity<>(new ApiResponse("contest schedule deleted", null), HttpStatus.OK);
    }

    @GetMapping("/leaderboard/{id}")
    public ResponseEntity<ApiResponse> getContestLeaderboard(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(new ApiResponse("", contestService.getLeaderboard(id)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @GetMapping("/created-contests")
    public ResponseEntity<ApiResponse> getCreatedContests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        List<ContestResponseDto> res = contestService.getCreatedContest(userId);
        return new ResponseEntity<>(new ApiResponse("", res), HttpStatus.OK);
    }

    @GetMapping("/assigned-contests")
    public ResponseEntity<ApiResponse> getAssignedContests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        List<ContestResponseDto> res = contestService.getAssignedContest(userId);
        return new ResponseEntity<>(new ApiResponse("", res), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @GetMapping("leaderboard/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("contestId") String contestId) throws IOException {

        Resource resource = contestService.getLeaderboardFile(contestId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=leaderboard_" + contestId + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

}
