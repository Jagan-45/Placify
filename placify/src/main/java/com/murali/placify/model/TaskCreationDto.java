package com.murali.placify.model;

import com.murali.placify.enums.TaskCron;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskCreationDto {
    private String taskName;
    @NotNull
    private List<String> batches;
    private LocalTime assignAtTime;
    private TaskCron repeat;
    @Future
    private LocalDate from;
    @Future
    private LocalDate to;
}
