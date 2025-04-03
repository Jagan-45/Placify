package com.murali.placify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.Mapper.ContestMapper;
import com.murali.placify.Mapper.UserScoreDto;
import com.murali.placify.cache.ContestCache;
import com.murali.placify.entity.*;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.enums.ContestUserStatus;
import com.murali.placify.enums.SubmissionStatus;
import com.murali.placify.model.*;
import com.murali.placify.repository.ContestRepository;
import com.murali.placify.repository.ContestSubmissionRepo;
import com.murali.placify.repository.ContestUserRepo;
import com.murali.placify.repository.dynamic.ContestLeaderboardRepo;
import com.murali.placify.scheduler.ContestScheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContestService {

    private final ProblemService problemService;
    private final UserService userService;
    private final ContestRepository contestRepository;
    private final SubmissionService submissionService;
    private final ContestScheduler contestScheduler;
    private final ContestSubmissionRepo contestSubmissionRepo;
    private final ContestLeaderboardRepo contestLeaderboardRepo;
    private final ContestUserRepo contestUserRepo;
    private final ContestMapper contestMapper;
    private final ContestCache contestCache;
    private final LeaderBoardService leaderBoardService;


    public ContestService(ProblemService problemService,
                          UserService userService,
                          ContestRepository contestRepository,
                          SubmissionService submissionService,
                          ContestScheduler contestScheduler,
                          ContestSubmissionRepo contestSubmissionRepo,
                          ContestLeaderboardRepo contestLeaderboardRepo,
                          ContestUserRepo contestUserRepo,
                          ContestMapper contestMapper,
                          ContestCache contestCache,
                          LeaderBoardService leaderBoardService) {
        this.problemService = problemService;
        this.userService = userService;
        this.contestRepository = contestRepository;
        this.submissionService = submissionService;
        this.contestScheduler = contestScheduler;
        this.contestSubmissionRepo = contestSubmissionRepo;
        this.contestLeaderboardRepo = contestLeaderboardRepo;
        this.contestUserRepo = contestUserRepo;
        this.contestMapper = contestMapper;
        this.contestCache = contestCache;
        this.leaderBoardService = leaderBoardService;
    }

    @Transactional
    public void CreateContest(ContestDto dto) {

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
            contestScheduler.scheduleContestEnd(c.getEndTime().toString(), c.getContestID());
        } catch (SchedulerException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error in creating scheduler");
        }
    }

    @Transactional
    public void startContest(UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest start, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.ACTIVE);

        contestRepository.save(contest);
    }

    @Transactional
    public void endContest(UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest end, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.CLOSED);

        contestRepository.save(contest);

        List<UserScoreDto> contestLeaderboard = contestLeaderboardRepo.getLeaderboard(contestId);

        if (contestLeaderboard.size() < 3) {
            contestLeaderboard.forEach(contestant -> {
                Leaderboard lb = leaderBoardService.getLeaderboardDataForUserId(contestant.getUserId());
                if (lb.getContestRating() > 0) {
                    lb.setContestRating(lb.getContestRating() + 10);
                    lb.setOverAllRating(lb.getOverAllRating() + 10);
                    leaderBoardService.saveRecord(lb);
                }
            });
        } else {
            contestLeaderboard.subList(0, 3).forEach(contestant -> {
                Leaderboard lb = leaderBoardService.getLeaderboardDataForUserId(contestant.getUserId());
                if (lb.getContestRating() > 0) {
                    lb.setContestRating(lb.getContestRating() + 10);
                    lb.setOverAllRating(lb.getOverAllRating() + 10);
                    leaderBoardService.saveRecord(lb);
                }
            });

            int remainingCount = contestLeaderboard.size() - 3;
            int groupSize = Math.max(1, remainingCount / 4);
            int[] pointsDistribution = {8, 6, 4, 2};

            for (int i = 0, group = 0; i < remainingCount; i++) {
                if (i >= (group + 1) * groupSize && group < 3) {
                    group++;
                }

                Leaderboard lb = leaderBoardService.getLeaderboardDataForUserId(contestLeaderboard.get(i + 3).getUserId());
                if (lb.getContestRating() > 0) {
                    lb.setContestRating(lb.getContestRating() + pointsDistribution[group]);
                    lb.setOverAllRating(lb.getOverAllRating() + pointsDistribution[group]);
                    leaderBoardService.saveRecord(lb);
                }
            }
        }
    }

    public List<SubmissionResult> submitContestProblem(UUID userId, ContestSubmissionDto dto) throws JsonProcessingException {

        Optional<Contest> optional = contestRepository.findById(dto.getContestId());
        if (optional.isEmpty())
            throw new IllegalArgumentException("No contest for given contestId");

        long submittedTime = Duration.between(optional.get().getStartTime(), LocalDateTime.now()).toMinutes();
        String intervalTime = submittedTime + " minutes";

        if (optional.get().getStatus() != ContestStatus.ACTIVE)
            throw new IllegalArgumentException("Contest is not active");
        if (!contestRepository.existsByContestIDAndUserAssignedTo_UserID(dto.getContestId(), userId))
            throw new IllegalArgumentException("You are not allowed to attend this contest");
        if (contestUserRepo.findByUser(userService.getUserById(userId)).getStatus() != ContestUserStatus.ENTERED)
            throw new IllegalArgumentException("You have exited the contest, you cannot submit problems");

        List<SubmissionResult> result = submissionService.ContestSubmission(userId, optional.get(), dto.getProblemId(), dto.getLanguageId(), dto.getCode());

        Problem problem = problemService.getProblemById(dto.getProblemId());

        ContestSubmission submission = new ContestSubmission();
        submission.setProblem(problem);
        submission.setContest(optional.get());
        submission.setUser(userService.getUserById(userId));
        submission.setContestSubmissionTime(submittedTime);

        if (result.stream().allMatch(r ->
                r.getStatus().getDescription().equals("Accepted")
        )) submission.setStatus(SubmissionStatus.ACCEPTED);


        contestSubmissionRepo.save(submission);

        try {
            HashMap<UUID, Set<UUID>> userAcProblemMap = contestCache.getContestUserAcProblemsCache().getOrDefault(dto.getContestId(), new HashMap<>());
            contestCache.getContestUserAcProblemsCache().put(dto.getContestId(), userAcProblemMap);

            Set<UUID> acceptedProblems = userAcProblemMap.get(userId);
            if (acceptedProblems != null && acceptedProblems.contains(dto.getProblemId())) {
                return result;
            }

            if (result.stream().allMatch(submissionResult -> submissionResult.getStatus().getId() == 3)) {
                System.out.println("Inside");

                Optional<UserScoreDto> optionalUserScoreDto = contestLeaderboardRepo.getUserScore(dto.getContestId(), userId);
                if (optionalUserScoreDto.isPresent()) {
                    UserScoreDto scoreDto = optionalUserScoreDto.get();
                    int score = scoreDto.getPoints() + problem.getPoints();
                    contestLeaderboardRepo.upsertUserScore(dto.getContestId(), userId, score, intervalTime);

                    Set<UUID> acProblemSet = userAcProblemMap.getOrDefault(userId, new HashSet<>());
                    acProblemSet.add(dto.getProblemId());

                    userAcProblemMap.put(userId, acProblemSet);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public Contest getContestById(UUID id) {
        return contestRepository.findById(id).get();
    }

    public void saveContest(Contest contest) {
        contestRepository.save(contest);
    }

    @Transactional
    public ContestResponseDto enterContest(UUID userId, UUID contestId) {
        ContestUser contestUser = contestUserRepo.findByUser(userService.getUserById(userId));

        if ((contestUser.getStatus() == ContestUserStatus.EXITED && contestUser.getExitCount() > 3) || contestUser.getStatus() == ContestUserStatus.SUBMITTED)
            throw new IllegalArgumentException("you have already entered the contest, you cannot enter again");

        contestLeaderboardRepo.insertScore(contestId, userId, 0, "0 minutes");
        contestUserRepo.updateStatus(contestId, userId, ContestUserStatus.ENTERED.name());
        return contestMapper.toDto(getContestById(contestId));
    }

    @Transactional
    public void exitContest(UUID userId, UUID contestId) {
        contestUserRepo.updateStatus(contestId, userId, ContestUserStatus.EXITED.name());
    }

    @Transactional
    public void submitContest(UUID userId, UUID contestId) {
        contestUserRepo.updateStatus(contestId, userId, ContestUserStatus.SUBMITTED.name());
    }

    public List<UserScoreDto> getLeaderboard(UUID id) {
        return contestLeaderboardRepo.getLeaderboard(id);
    }

    public List<Contest> getAllScheduledContests() {
        return contestRepository.findAll();
    }

    public void updateScheduledContest(UUID userId, ContestUpdateDto dto) {

        User createdBy = userService.getUserById(userId);

        if (!contestRepository.existsByCreatedByAndContestID(createdBy, dto.getContestId()))
            throw new IllegalArgumentException("Wrong contest id or You have not created this contest");

        if (getContestById(dto.getContestId()).getStatus() != ContestStatus.SCHEDULED)
            throw new IllegalArgumentException("Contest has already completed or being active cannot update now");

        try {
            contestScheduler.update(dto.getStartTime().toString(), dto.getEndTime().toString(), dto.getContestId());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot update contest, please try again");
        }
    }

    public void deleteScheduledContest(UUID createdBy, UUID id) {

        if (!contestRepository.existsByCreatedByAndContestID(userService.getUserById(createdBy), id))
            throw new IllegalArgumentException("Wrong contest id or You have not created this contest");

        if (getContestById(id).getStatus() != ContestStatus.SCHEDULED)
            throw new IllegalArgumentException("Contest has already completed or being active cannot update now");
        try {
            contestScheduler.deleteContest(id);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot delete contest, please try again");
        }
    }
}
