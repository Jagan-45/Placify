package com.murali.placify.scheduler;

import com.murali.placify.entity.Contest;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Component
public class ContestScheduler {
    private final Scheduler scheduler;

    public ContestScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleContestStart(String contestTime, UUID contestId) throws SchedulerException {
        LocalDateTime localDateTime = LocalDateTime.parse(contestTime, DateTimeFormatter.ISO_DATE_TIME);
        Date contestDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("contestId", contestId);

        JobDetail job = JobBuilder.newJob(ContestStartJob.class)
                .withIdentity("contestStartJob", "contestGroup")
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("contestStartTrigger", "contestGroup")
                .startAt(contestDate)
                .build();


        scheduler.start();
        scheduler.scheduleJob(job, trigger);
        System.out.println("Contest Scheduled for: " + contestTime);
    }

    public void scheduleContestEnd() {

    }
}
