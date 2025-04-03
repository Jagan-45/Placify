package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSubmissionDto {
    private UUID problemId;
    private int languageId;
    private String code;
}
