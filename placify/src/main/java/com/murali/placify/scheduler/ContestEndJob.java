package com.murali.placify.scheduler;


import com.murali.placify.entity.Contest;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.service.ContestService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

public class ContestEndJob implements Job {

    @Autowired
    private ContestService contestService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String contestIdStr = jobDataMap.getString("contestId");
        UUID contestId = UUID.fromString(contestIdStr);

        contestService.endContest(contestId);

        System.out.println("Contest ended at: " + LocalDateTime.now());
    }
}
