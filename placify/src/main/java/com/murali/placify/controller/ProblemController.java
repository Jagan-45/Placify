package com.murali.placify.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.model.ProblemSubmissionDto;
import com.murali.placify.model.SubmissionResult;
import com.murali.placify.service.ProblemService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v0/problem")
@RestController
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/submit-sample")
    public List<SubmissionResult> submitProblemWithTestcase(@RequestBody ProblemSubmissionDto dto) throws JsonProcessingException {
        return problemService.submitCode(dto);
    }
}
