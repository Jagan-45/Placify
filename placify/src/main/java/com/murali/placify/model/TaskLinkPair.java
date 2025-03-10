package com.murali.placify.model;

import com.murali.placify.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskLinkPair {
    private Task task;
    private TaskWithProblemLinksDTO dto;
}
