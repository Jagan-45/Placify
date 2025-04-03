package com.murali.placify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murali.placify.model.SubmissionModel;
import com.murali.placify.model.SubmissionResult;
import com.murali.placify.model.TokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class JudgeService {

    private final WebClient webClient;

    public JudgeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<SubmissionResult> submitCode(String code, int lang_id, List<String> inputs, List<String> outputs, List<String> tcNames)
            throws JsonProcessingException {
        List<String> tokens = new ArrayList<>();
        Map<String, String> tokenToTcName = new HashMap<>();

        for (int i = 0; i < inputs.size(); i++) {
            String token = submitToJudge(code, lang_id, inputs.get(i), outputs.get(i));
            tokens.add(token);
            tokenToTcName.put(token, tcNames.get(i));
        }

        List<CompletableFuture<SubmissionResult>> futures = new ArrayList<>();
        for (String token : tokens) {
            String tcName = tokenToTcName.get(token);
            futures.add(pollSubmissionResultWithRetry(token, tcName));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<SubmissionResult> results = new ArrayList<>();
        futures.forEach(future -> results.add(future.join()));

        return results;
    }

    public String submitToJudge(String code, int languageId, String stdin, String expectedOutput) {
        System.out.println("STDIN: " + stdin);
        System.out.println("Expected Output: " + expectedOutput);

        SubmissionModel submissionModel = new SubmissionModel();
        submissionModel.setSource_code(code);
        submissionModel.setStdin(stdin);
        submissionModel.setLanguage_id(languageId);
        submissionModel.setExpected_output(expectedOutput);

        TokenResponse tokenResponse = webClient.post()
                .uri("http://localhost:2358/submissions?base64encode=true")
                .header("Content-Type", "application/json")
                .bodyValue(submissionModel)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (tokenResponse == null || tokenResponse.getToken() == null) {
            throw new RuntimeException("Failed to get submission token.");
        }

        return tokenResponse.getToken();
    }

    private SubmissionResult pollSubmissionResult(String token) throws JsonProcessingException {
        String rawResponse = webClient.get()
                .uri("http://localhost:2358/submissions/{token}?base64encode=true", token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Polling token " + token + " => " + rawResponse);
        SubmissionResult result = new ObjectMapper().readValue(rawResponse, SubmissionResult.class);
        return result;
    }

    private CompletableFuture<SubmissionResult> pollSubmissionResultWithRetry(String token, String tcName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SubmissionResult result = pollSubmissionResult(token);

                while (result.getStatus().getId() == 1 || result.getStatus().getId() == 2) {
                    Thread.sleep(500);
                    result = pollSubmissionResult(token);
                }
                result.setTcName(tcName);
                return result;
            } catch (JsonProcessingException | InterruptedException e) {
                throw new CompletionException(e);
            }
        });
    }
}
