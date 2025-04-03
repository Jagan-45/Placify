package com.murali.placify.model;

import com.murali.placify.enums.TaskCron;
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
    private List<String> batches;
    private LocalTime assignAtTime;
    private TaskCron repeat;
    private LocalDate from;
    private LocalDate to;
}
