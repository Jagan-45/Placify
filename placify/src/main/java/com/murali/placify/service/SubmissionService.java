package com.murali.placify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.entity.Contest;
import com.murali.placify.entity.Testcase;
import com.murali.placify.enums.SubmissionType;
import com.murali.placify.model.SubmissionResult;
import com.murali.placify.response.TestcaseResponse;
import com.murali.placify.util.SourceCodeModifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionService {

    private final TestcaseService testcaseService;
    private final JudgeService judgeService;
    private final SourceCodeModifier sourceCodeModifier;

    public SubmissionService(TestcaseService testcaseService, JudgeService judgeService, SourceCodeModifier sourceCodeModifier) {
        this.testcaseService = testcaseService;
        this.judgeService = judgeService;
        this.sourceCodeModifier = sourceCodeModifier;
    }

    public List<SubmissionResult> ContestSubmission(UUID userId, Contest contest, UUID problemId, int languageId, String code) {
        try {
            return submitCode(problemId, languageId, code, SubmissionType.COMPLETE);
        }
        catch (Exception e) {
            throw new RuntimeException("try again, an error occurred");
        }
    }

    public List<SubmissionResult> submitCode(UUID problemId, int languageId, String sourceCode, SubmissionType type) throws JsonProcessingException {
        String code = sourceCodeModifier.addImports(languageId, sourceCode);
        
        List<TestcaseResponse> testcaseList;

        if (type.equals(SubmissionType.COMPLETE))
            testcaseList = testcaseService.getAllTestcases(problemId);
        else testcaseList = testcaseService.getSampleTestcases(problemId);

        List<String> inputs = new ArrayList<>(testcaseList.size());
        List<String> outputs = new ArrayList<>(testcaseList.size());
        List<String> tcNames = new ArrayList<>(testcaseList.size());

        testcaseList.forEach(testcaseResponse -> {
                    String name = testcaseResponse.getTcName();
                    String input = testcaseResponse.getInput();
                    String output = testcaseResponse.getOutput();
                    StringBuilder in = new StringBuilder();
                    StringBuilder out = new StringBuilder();

                    for (char c : input.toCharArray()) {
                        if (c != '[' && c != ']' && c != ',')
                            in.append(c);
                    }
                    for (char c : output.toCharArray()) {
                        if (c != '[' && c != ']' && c != ',')
                            out.append(c);
                    }

                    inputs.add(in.toString());
                    outputs.add(out.toString());
                    tcNames.add(name);
                }
        );

        return judgeService.submitCode(code, languageId, inputs, outputs, tcNames);
    }
}
