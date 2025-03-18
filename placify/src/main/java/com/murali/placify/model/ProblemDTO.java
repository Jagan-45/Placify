package com.murali.placify.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDTO {

    @NotBlank(message = "problem shouldn't be blank")
    private String problemName;

    @NotBlank(message = "description shouldn't be blank")
    @Size(min = 1, max = 1000, message = "Enter description with valid length, length exceeded")
    private String description;

    @NotBlank(message = "Slug shouldn't be blank")
    private String problemSlug;

    @NotBlank
    private String inputFields;
    @NotBlank
    private String outputField;
    @NotBlank
    private String constrains;

    private int points;
    @Valid
    private List<TestcaseDTO> testcases;
    private String createdBy;

}
