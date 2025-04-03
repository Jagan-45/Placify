package com.murali.placify.scheduler;

import com.murali.placify.entity.Contest;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.repository.dynamic.ContestLeaderboardRepo;
import com.murali.placify.service.ContestService;
import lombok.Getter;
import lombok.Setter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ContestStartJob implements Job{

    @Autowired
    private ContestService contestService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String contestIdStr = jobDataMap.getString("contestId");
        UUID contestId = UUID.fromString(contestIdStr);

        contestService.startContest(contestId);

        System.out.println("Contest Started at: " + LocalDateTime.now());
    }
}
