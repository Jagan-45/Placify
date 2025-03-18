package com.murali.placify.model;

import jakarta.validation.Valid;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID createdBy;
    private List<String> assignToBatches;

    @Valid
    private List<ProblemDTO> problems;
}
