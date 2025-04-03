package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestSubmissionDto {
    private UUID contestId;
    private UUID problemId;
    private int languageId;
    private String code;
}
