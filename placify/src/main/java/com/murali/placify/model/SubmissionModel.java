package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubmissionModel {
    private String source_code;
    private int language_id;
    private String stdin;
    private String expected_output;
}
