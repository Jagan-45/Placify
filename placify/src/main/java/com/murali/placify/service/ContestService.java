package com.murali.placify.service;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.Problem;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.exception.FileException;
import com.murali.placify.model.ContestDto;
import com.murali.placify.model.ProblemDTO;
import com.murali.placify.repository.ContestRepository;
import com.murali.placify.scheduler.ContestScheduler;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContestService {

    private final ProblemService problemService;
    private final UserService userService;
    private final ContestRepository contestRepository;

    private final ContestScheduler contestScheduler;

    public ContestService(ProblemService problemService, UserService userService, ContestRepository contestRepository, ContestScheduler contestScheduler) {
        this.problemService = problemService;
        this.userService = userService;
        this.contestRepository = contestRepository;
        this.contestScheduler = contestScheduler;
    }

    @Transactional
    public void CreateContest (ContestDto dto) {

        Contest contest = new Contest();
        contest.setContestName(dto.getContestName());
        contest.setCreatedDate(dto.getCreatedAt());
        contest.setStartTime(dto.getStartTime());
        contest.setEndTime(dto.getEndTime());
        contest.setStatus(ContestStatus.SCHEDULED);
        contest.setCreatedBy(userService.getUserById(dto.getCreatedBy()));
        contest.setUserAssignedTo(userService.getUserByBatch(dto.getAssignToBatches()));
        List<Problem> problems = problemService.createProblems(dto.getProblems());
        problems.forEach(problem -> problem.setVisible(false));
        contest.setProblemList(problems);


        Contest c = contestRepository.saveAndFlush(contest);
        problemService.saveProblemMDFiles(problems);

        try {
            contestScheduler.scheduleContestStart(c.getStartTime().toString(), c.getContestID());
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Error in creating scheduler");
        }
    }

    @Transactional
    public void startContest (UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.ACTIVE);

        contestRepository.save(contest);
    }

    @Transactional
    public void endContest (UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.CLOSED);

        contestRepository.save(contest);
    }
}
