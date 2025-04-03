package com.murali.placify.runner;

import com.murali.placify.Mapper.TaskScheduledMapper;
import com.murali.placify.entity.Contest;
import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.repository.ContestRepository;
import com.murali.placify.scheduler.ContestScheduler;
import com.murali.placify.scheduler.TaskScheduler;
import com.murali.placify.service.ContestService;
import com.murali.placify.service.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SchedulerRunner implements ApplicationRunner {

    private final TaskService taskService;
    private final TaskScheduledMapper taskScheduledMapper;
    private final TaskScheduler taskScheduler;
    private final ContestRepository contestRepository;
    private final ContestService contestService;
    private final ContestScheduler contestScheduler;

    public SchedulerRunner(TaskService taskService, TaskScheduledMapper taskScheduledMapper, TaskScheduler taskScheduler, ContestRepository contestRepository, ContestService contestService, ContestScheduler contestScheduler) {
        this.taskService = taskService;
        this.taskScheduledMapper = taskScheduledMapper;
        this.taskScheduler = taskScheduler;
        this.contestRepository = contestRepository;
        this.contestService = contestService;
        this.contestScheduler = contestScheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<TaskScheduled> taskScheduledList =  taskService.getAllScheduledTasks();

        for(TaskScheduled ts : taskScheduledList) {
            if (ts.getToDate().isAfter(LocalDate.now()))
                taskScheduler.scheduleTask(ts, ts.getCreatedBy().getUserID(), taskScheduledMapper.toDto(ts));
        }

        List<Contest> contestsScheduled = contestService.getAllScheduledContests();

        for (Contest cs : contestsScheduled) {
            if (cs.getStatus() == ContestStatus.ACTIVE)
                contestScheduler.scheduleContestEnd(cs.getEndTime().toString(), cs.getContestID());
            if (cs.getStatus() == ContestStatus.SCHEDULED) {
                contestScheduler.scheduleContestStart(cs.getStartTime().toString(), cs.getContestID());
                contestScheduler.scheduleContestEnd(cs.getEndTime().toString(), cs.getContestID());
            }
        }
    }
}
