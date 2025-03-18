package com.murali.placify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProblemResponse {
    private String problemName;
    private String ProblemSlug;
    private int points;
    private String description;
    private String constraints;
    private String inputFields;
    private String outputField;
}
