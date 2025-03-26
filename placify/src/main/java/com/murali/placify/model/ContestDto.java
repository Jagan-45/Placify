package com.murali.placify.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ContestDto {
    private String contestName;
    private LocalDateTime createdAt;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be a future date")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be a future date")
    private LocalDateTime endTime;

    private UUID createdBy;
    private List<String> assignToBatches;

    @Valid
    private List<ProblemDTO> problems;
}
