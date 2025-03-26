package com.murali.placify.scheduler;

import com.murali.placify.service.ContestService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ContestStartJob implements Job {

    @Autowired
    private ContestService contestService;

    public ContestStartJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        UUID id = (UUID) jobDataMap.get("contestId");

        System.out.println("Contest Started at: " + LocalDateTime.now());

    }
}