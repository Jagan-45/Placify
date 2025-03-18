package com.murali.placify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestcaseResponse {
    private String tcName;
    private String inputFields;
    private String outputFields;
    private String input;
    private String output;
    private String explanation;
}
