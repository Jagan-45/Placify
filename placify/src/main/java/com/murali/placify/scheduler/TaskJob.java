package com.murali.placify.scheduler;

import com.murali.placify.entity.Task;
import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.model.TaskCreationDto;
import com.murali.placify.service.TaskService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@DisallowConcurrentExecution
public class TaskJob implements Job {

    @Autowired
    private TaskService taskService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        UUID createdBy = (UUID) jobDataMap.get("createdBy");
        TaskCreationDto dto = (TaskCreationDto) jobDataMap.get("dto");
        String jobId = (String) jobDataMap.get("jobId");
        String triggerId = (String) jobDataMap.get("triggerId");
        String cronExp = (String) jobDataMap.get("cronExp");
        TaskScheduled ts = (TaskScheduled) jobDataMap.get("ts");

        List<Task> tasksCreated = taskService.createAutomatedTasks(createdBy, dto, ts);

        taskService.saveAssociatedTasks(ts, tasksCreated, dto, jobId, triggerId, cronExp, createdBy);
    }
}
