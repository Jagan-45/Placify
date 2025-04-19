package com.murali.placify.model;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProblemRequirement {

    @NotNull
    @NotBlank
    private String topicTag;

    @NotNull
    @NotBlank
    private String difficulty;

    @Min(1)
    @Max(4)
    private int count;

    @Min(1)
    private int point;
}
