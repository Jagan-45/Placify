package com.murali.placify.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestUpdateDto {

    private UUID contestId;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be a future date")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be a future date")
    private LocalDateTime endTime;
}
