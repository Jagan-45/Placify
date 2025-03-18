package com.murali.placify.service;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.Problem;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.exception.FileException;
import com.murali.placify.model.ContestDto;
import com.murali.placify.model.ProblemDTO;
import com.murali.placify.repository.ContestRepository;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContestService {

    private final ProblemService problemService;
    private final UserService userService;
    private final ContestRepository contestRepository;

    public ContestService(ProblemService problemService, UserService userService, ContestRepository contestRepository) {
        this.problemService = problemService;
        this.userService = userService;
        this.contestRepository = contestRepository;
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
        contest.setProblemList(problemService.createProblems(dto.getProblems()));

        contestRepository.save(contest);

    }
}
