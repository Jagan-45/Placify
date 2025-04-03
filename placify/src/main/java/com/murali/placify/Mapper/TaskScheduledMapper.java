package com.murali.placify.Mapper;

import com.murali.placify.model.TaskCreationDto;
import com.murali.placify.entity.TaskScheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskScheduledMapper {

    public TaskCreationDto toDto(TaskScheduled taskScheduled) {
        if (taskScheduled == null) {
            return null;
        }
        List<String> batchNames = new ArrayList<>();

        TaskCreationDto dto = new TaskCreationDto();
        dto.setFrom(taskScheduled.getFromDate());
        dto.setTo(taskScheduled.getToDate());
        dto.setAssignAtTime(taskScheduled.getScheduleTime());
        dto.setRepeat(taskScheduled.getRepeat());

        taskScheduled.getBatches().forEach(b -> batchNames.add(b.getBatchName()));

        dto.setBatches(batchNames);

        return dto;
    }

    public TaskScheduled toEntity(TaskCreationDto dto, String jobKey, String triggerKey, String cronExp) {
        if (dto == null) {
            return null;
        }

        TaskScheduled taskScheduled = new TaskScheduled();
        taskScheduled.setFromDate(dto.getFrom());
        taskScheduled.setToDate(dto.getTo());
        taskScheduled.setScheduleTime(dto.getAssignAtTime());
        taskScheduled.setCronExpression(cronExp);
        taskScheduled.setJobKey(jobKey);
        taskScheduled.setTriggerKey(triggerKey);
        return taskScheduled;
    }
}

