package com.murali.placify.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeetcodeApiService {

    private final WebClient webClient;

    public LeetcodeApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean doesUserExist(String username) {
        try {
            String response = webClient.get()
                    .uri("http://localhost:3000/{username}", username)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject json = new JSONObject(response);

            if (json.has("errors") ||
                    json.optJSONObject("data") != null && json.getJSONObject("data").isNull("matchedUser")) {
                return false;
            }
            return json.has("username");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Map<String, String>> getLast20Submissions(String username) {
        List<Map<String, String>> submissions = new ArrayList<>();
        try {
            String response = webClient.get()
                    .uri("http://localhost:3000/{username}/acSubmission", username)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject json = new JSONObject(response);
            JSONArray submissionArray = json.optJSONArray("submission");

            if (submissionArray != null) {
                for (int i = 0; i < submissionArray.length(); i++) {
                    JSONObject obj = submissionArray.getJSONObject(i);
                    String titleSlug = obj.optString("titleSlug");
                    String timestamp = obj.optString("timestamp");
                    Map<String, String> result = new HashMap<>();
                    result.put("titleSlug", titleSlug);
                    result.put("timeStamp", timestamp);
                    submissions.add(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submissions;
    }

    public boolean isCompleted(String username, String assignedProblem, LocalDate assignedAt) {
        List<Map<String, String>> result = getLast20Submissions(username);

        String[] assignedProblemLink = assignedProblem.split("/");
        String assignedProblemSlug = assignedProblemLink[assignedProblemLink.length - 1];

        return result.stream().anyMatch(r ->
        {
            LocalDate completedAt = convertTimestampToDate(r.get("timeStamp"));
            return assignedProblemSlug.equals(r.get("titleSlug")) && completedAt.equals(assignedAt);
        });

    }

    private static LocalDate convertTimestampToDate(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
