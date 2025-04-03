package com.murali.placify.scheduler;

import ch.qos.logback.core.util.DynamicClassLoadingException;
import com.murali.placify.exception.DynamicTableCreationException;
import com.murali.placify.repository.dynamic.ContestLeaderboardRepo;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Component
public class ContestScheduler {

    private final Scheduler scheduler;
    private final ContestLeaderboardRepo contestLeaderboardRepo;

    public ContestScheduler(Scheduler scheduler, ContestLeaderboardRepo contestLeaderboardRepo) {
        this.scheduler = scheduler;
        this.contestLeaderboardRepo = contestLeaderboardRepo;
    }

    public void scheduleContestStart(String contestTime, UUID contestId) throws SchedulerException {

        try {
            contestLeaderboardRepo.createLeaderboardTable(contestId);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new DynamicTableCreationException("Cannot create leaderboard table, \nscheduling failed. Try again");
        }

        LocalDateTime localDateTime = LocalDateTime.parse(contestTime, DateTimeFormatter.ISO_DATE_TIME);
        Date contestDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("contestId", contestId.toString());

        JobKey jobKey = new JobKey("contestStartJob-" + contestId, "contestGroup");
        TriggerKey triggerKey = new TriggerKey("contestStartTrigger-" + contestId, "contestGroup");

        JobDetail job = JobBuilder.newJob(ContestStartJob.class)
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(contestDate)
                .forJob(job)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(job, trigger);

        System.out.println("Contest Scheduled for: " + contestTime);
    }

    public void scheduleContestEnd(String endTime, UUID contestId) throws SchedulerException {
        LocalDateTime localDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
        Date contestDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("contestId", contestId.toString());

        JobKey jobKey = new JobKey("contestEndJob-" + contestId, "contestGroup");
        TriggerKey triggerKey = new TriggerKey("contestEndTrigger-" + contestId, "contestGroup");

        JobDetail job = JobBuilder.newJob(ContestEndJob.class)
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(contestDate)
                .forJob(job)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(job, trigger);
        System.out.println("Contest END Scheduled for: " + endTime);
    }

    private void removeScheduledJobs(UUID contestId) throws SchedulerException {

        JobKey startJobKey = new JobKey("contestStartJob-" + contestId, "contestGroup");
        TriggerKey startTriggerKey = new TriggerKey("contestStartTrigger-" + contestId, "contestGroup");
        JobKey endJobKey = new JobKey("contestEndJob-" + contestId, "contestGroup");
        TriggerKey endTriggerKey = new TriggerKey("contestEndTrigger-" + contestId, "contestGroup");

        if (scheduler.checkExists(startTriggerKey)) {
            scheduler.unscheduleJob(startTriggerKey);
        }
        if (scheduler.checkExists(startJobKey)) {
            scheduler.deleteJob(startJobKey);
        }

        if (scheduler.checkExists(endTriggerKey)) {
            scheduler.unscheduleJob(endTriggerKey);
        }
        if (scheduler.checkExists(endJobKey)) {
            scheduler.deleteJob(endJobKey);
        }
    }

    public void update(String startTime, String endTime, UUID contestId) throws SchedulerException {

        removeScheduledJobs(contestId);

        scheduleContestStart(startTime, contestId);
        scheduleContestEnd(endTime, contestId);

        System.out.println("Contest schedule updated for Contest ID: " + contestId);
    }

    public void deleteContest(UUID contestId) throws SchedulerException {
        removeScheduledJobs(contestId);
        System.out.println("Contest deleted for Contest ID: " + contestId);
    }
}
