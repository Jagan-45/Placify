package com.murali.placify.service;

import com.murali.placify.entity.User;
import com.murali.placify.exception.ContestCreationException;
import com.murali.placify.model.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ContestCreationHelper {
    private final WebClient webClient;

    public ContestCreationHelper(WebClient webClient) {
        this.webClient = webClient;
    }

    public ContestDto createContest(User createdBy, CreateContestDto dto) {

        if (dto.getEndTime().isBefore(dto.getStartTime()))
            throw new IllegalArgumentException("Contest end time should be after start time");

        List<ProblemRequirement> requirement = dto.getReq();
        List<ProblemDTO> problemDTOs = getProblems(createdBy, requirement);
        //System.out.println("problem DTO --->" + problemDTOs);
        ContestDto contestDto = new ContestDto();

        contestDto.setContestName(dto.getContestName());
        contestDto.setCreatedAt(LocalDateTime.now());
        contestDto.setCreatedBy(createdBy.getUserID());
        contestDto.setStartTime(dto.getStartTime());
        contestDto.setEndTime(dto.getEndTime());
        contestDto.setAssignToBatches(dto.getAssignToBatches());
        contestDto.setProblems(problemDTOs);

        return contestDto;
    }

    private List<ProblemDTO> getProblems(User createdBy, List<ProblemRequirement> requirements) {
        List<CompletableFuture<ProblemDTO>> futures = new ArrayList<>();
        for (ProblemRequirement req : requirements) {
            int countNeeded = req.getCount();
            for (int i = 0; i < countNeeded; i++) {
                CompletableFuture<ProblemDTO> future = CompletableFuture.supplyAsync(() -> {
                    int retryCount = 3;
                    Map<String, Object> generatedProblem = null;
                    req.setCount(1);
                    while (retryCount > 0) {
                        try {
                            generatedProblem = callProblemGenerator(req);
                            break;
                        } catch (Exception ex) {
                            retryCount--;
                            if (retryCount == 0) {
                                ex.printStackTrace();
                                throw new ContestCreationException("Unable to create contest, error generating problem for requirement: " + req);
                            }
                        }
                    }
                    ProblemDTO dto = mapToProblemDTO(generatedProblem, createdBy);
                    dto.setPoints(req.getPoint());
                    dto.setProblemSlug(dto.getTestcases().get(0).getProblemSlug());
                    return dto;
                });
                futures.add(future);
            }
        }
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private Map<String, Object> callProblemGenerator(ProblemRequirement req) {
        String result = webClient.post()
                .uri("http://localhost:8001/generate")
                .header("Content-Type", "application/json")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JSONObject jsonObject = new JSONObject(result);
        return jsonObject.toMap();
    }

    private ProblemDTO mapToProblemDTO(Map<String, Object> generatedProblem, User createdBy) {
        UUID randomUUID = UUID.randomUUID();

        List<Map<String, Object>> problems = (List<Map<String, Object>>) generatedProblem.get("problems");
        if (problems == null || problems.isEmpty()) {
            throw new ContestCreationException("No problem generated in the API response.");
        }
        Map<String, Object> problemData = problems.get(0);
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setProblemName((String) problemData.get("problemName"));
        problemDTO.setDescription((String) problemData.get("description"));
        problemDTO.setInputFields((String) problemData.get("inputFields"));
        problemDTO.setOutputField((String) problemData.get("outputField"));
        problemDTO.setConstrains((String) problemData.get("constraints"));

        List<Map<String, Object>> testcasesList = (List<Map<String, Object>>) problemData.get("testcases");
        List<TestcaseDTO> testcaseDTOs = new ArrayList<>();
        if (testcasesList != null) {
            for (Map<String, Object> tc : testcasesList) {
                TestcaseDTO testcaseDTO = new TestcaseDTO();
                testcaseDTO.setTcName((String) tc.get("tcName"));
                testcaseDTO.setInputFields((String) tc.get("inputFields"));
                testcaseDTO.setOutputField((String) tc.get("outputField"));
                Object sampleValue = tc.get("sample");
                if (sampleValue instanceof Number) {
                    testcaseDTO.setSample(((Number) sampleValue).intValue() == 1);
                } else {
                    testcaseDTO.setSample(Boolean.parseBoolean(sampleValue.toString()));
                }
                testcaseDTO.setExplanation((String) tc.get("explanation"));
                testcaseDTO.setProblemSlug((String) tc.get("problemSlug") + randomUUID);
                testcaseDTOs.add(testcaseDTO);
            }
        }
        problemDTO.setTestcases(testcaseDTOs);
        problemDTO.setCreatedBy(createdBy.getUserID().toString());
        return problemDTO;
    }


}
