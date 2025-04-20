package com.murali.placify.scheduler;

import com.murali.placify.entity.Task;
import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.enums.TaskCron;
import com.murali.placify.model.TaskCreationDto;
import com.murali.placify.repository.BatchRepository;
import com.murali.placify.repository.TaskScheduledRepo;
import com.murali.placify.service.TaskService;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TaskScheduler {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskScheduledRepo taskScheduledRepo;

    public TaskScheduled scheduleTask(TaskScheduled ts, UUID createdBy, TaskCreationDto dto) {
        LocalDate from = dto.getFrom();
        LocalDate to = dto.getTo();
        LocalTime scheduleAt = dto.getAssignAtTime();
        TaskCron repeat = dto.getRepeat();

        String jobId = "taskJob_" + UUID.randomUUID();
        String triggerId = "taskJob_" + UUID.randomUUID();
        String cronExpression = generateCronExpression(repeat, scheduleAt);

        ts.setTriggerKey(triggerId);
        ts.setJobKey(jobId);
        ts.setCronExpression(cronExpression);
        ts.setTaskName(dto.getTaskName());

        TaskScheduled taskScheduled = taskScheduledRepo.save(ts);

        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("createdBy", createdBy);
            jobDataMap.put("dto", dto);
            jobDataMap.put("jobId", jobId);
            jobDataMap.put("triggerId", triggerId);
            jobDataMap.put("cronExp", cronExpression);
            jobDataMap.put("ts", taskScheduled);

            JobDetail jobDetail = JobBuilder.newJob(TaskJob.class)
                    .withIdentity(jobId, "group1")
                    .usingJobData(jobDataMap)
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerId, "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .startAt(Date.from(from.atTime(scheduleAt).atZone(ZoneId.systemDefault()).toInstant()))
                    .endAt(Date.from(to.atTime(scheduleAt).atZone(ZoneId.systemDefault()).toInstant()))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);  // Schedule job with trigger
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return taskScheduled;
    }

    private String generateCronExpression(TaskCron repeat, LocalTime scheduleAt) {
        int hour = scheduleAt.getHour();
        int minute = scheduleAt.getMinute();
        int second = scheduleAt.getSecond();

        if (repeat == TaskCron.EVERYDAY) {
            return String.format("%d %d %d ? * *", second, minute, hour);
        } else if (repeat == TaskCron.ALTERNATE) {
            return String.format("%d %d %d ? * MON,WED,FRI", second, minute, hour);
        } else {
            return String.format("%d %d %d ? * MON-FRI", second, minute, hour);
        }
    }

    public TaskScheduled updateTaskSchedule(TaskScheduled ts, TaskCreationDto dto) {
        try {
            String jobId = ts.getJobKey();
            String triggerId = ts.getTriggerKey();

            TriggerKey triggerKey = new TriggerKey(triggerId, "group1");
            JobKey jobKey = new JobKey(jobId, "group1");

            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            LocalDate from = dto.getFrom();
            LocalDate to = dto.getTo();
            LocalTime scheduleAt = dto.getAssignAtTime();
            TaskCron repeat = dto.getRepeat();


            String newJobId = "taskJob_" + UUID.randomUUID();
            String newTriggerId = "taskTrigger_" + UUID.randomUUID();
            String newCronExpression = generateCronExpression(repeat, scheduleAt);

            ts.setJobKey(newJobId);
            ts.setTriggerKey(newTriggerId);
            ts.setCronExpression(newCronExpression);
            ts.setScheduleTime(scheduleAt);

            TaskScheduled updatedTask = taskScheduledRepo.save(ts);

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("createdBy", ts.getCreatedBy().getUserID());
            jobDataMap.put("dto", dto);
            jobDataMap.put("jobId", newJobId);
            jobDataMap.put("triggerId", newTriggerId);
            jobDataMap.put("cronExp", newCronExpression);
            jobDataMap.put("ts", updatedTask);

            JobDetail jobDetail = JobBuilder.newJob(TaskJob.class)
                    .withIdentity(newJobId, "group1")
                    .usingJobData(jobDataMap)
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(newTriggerId, "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                    .startAt(Date.from(from.atTime(scheduleAt).atZone(ZoneId.systemDefault()).toInstant()))
                    .endAt(Date.from(to.atTime(scheduleAt).atZone(ZoneId.systemDefault()).toInstant()))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            System.out.println("______updated______");
            return updatedTask;
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot update task schedule, internal error");
        }

    }

    public void deleteTask(TaskScheduled ts) {
        try {
            String jobId = ts.getJobKey();
            String triggerId = ts.getTriggerKey();

            TriggerKey triggerKey = new TriggerKey(triggerId, "group1");
            JobKey jobKey = new JobKey(jobId, "group1");

            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
