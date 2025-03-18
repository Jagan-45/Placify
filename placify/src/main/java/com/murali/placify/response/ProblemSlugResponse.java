package com.murali.placify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSlugResponse {
    private String problemName;
    private String problemSlug;
    private int points;
}
