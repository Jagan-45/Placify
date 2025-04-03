package com.murali.placify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SubmissionResult {
    private String tcName = null;
    private String stdout;
    private String time;
    private int memory;
    private String stderr;
    @JsonIgnore
    private String token;
    private String compile_output;
    private String message;
    private Status status;
}
