package com.murali.placify.model;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateContestDto {

    @NotNull
    @NotBlank
    private String contestName;

    @Future
    private LocalDateTime startTime;

    @Future
    private LocalDateTime endTime;

    @NotNull
    @NotEmpty
    private List<String> assignToBatches;

    @Valid
    @NotNull
    @NotEmpty
    private List<ProblemRequirement> req;
}
