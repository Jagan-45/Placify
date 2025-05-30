package com.murali.placify.service;

import com.murali.placify.Mapper.ContestMapper;
import com.murali.placify.Mapper.UserScoreDto;
import com.murali.placify.cache.ContestCache;
import com.murali.placify.entity.*;
import com.murali.placify.enums.*;
import com.murali.placify.model.*;
import com.murali.placify.repository.ContestRepository;
import com.murali.placify.repository.ContestSubmissionRepo;
import com.murali.placify.repository.ContestUserRepo;
import com.murali.placify.repository.ContestUserRepository;
import com.murali.placify.repository.dynamic.ContestLeaderboardRepo;
import com.murali.placify.scheduler.ContestScheduler;
import com.murali.placify.util.ExcelExporter;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.quartz.SchedulerException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final AuthService authService;
    private final ContestUserRepository contestUserRepository;
    private final ContestCreationHelper contestCreationHelper;
    private final ExcelExporter excelExporter;


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
                          LeaderBoardService leaderBoardService, AuthService authService, ContestUserRepository contestUserRepository, ContestCreationHelper contestCreationHelper, ExcelExporter excelExporter) {
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
        this.authService = authService;
        this.contestUserRepository = contestUserRepository;
        this.contestCreationHelper = contestCreationHelper;
        this.excelExporter = excelExporter;
    }

    public void createContestAutomated(UUID createdBy, CreateContestDto dto) {

        if (dto.getStartTime().isBefore(LocalDateTime.now().plusMinutes(5)))
            throw new IllegalArgumentException("Please make sure the start time is 5 minutes from now, " +
                    "this allows the application to generate problems and facilitate other tasks");

        ContestDto contestDto = contestCreationHelper.createContest(userService.getUserById(createdBy), dto);
        createContest(contestDto);
    }

    @Transactional
    public void createContest(ContestDto dto) {

        Contest contest = new Contest();
        contest.setContestName(dto.getContestName());
        contest.setCreatedDate(dto.getCreatedAt());
        contest.setStartTime(dto.getStartTime());
        contest.setEndTime(dto.getEndTime());
        contest.setStatus(ContestStatus.SCHEDULED);
        contest.setCreatedBy(userService.getUserById(dto.getCreatedBy()));

        List<Problem> problems = problemService.createProblems(dto.getProblems());
        problems.forEach(p -> p.setVisible(false));
        contest.setProblemList(problems);

        Contest savedContest = contestRepository.saveAndFlush(contest);

        List<User> assignedUsers = userService.getUserByBatch(dto.getAssignToBatches());
        List<ContestUser> contestUsers = new ArrayList<>();

        for (User user : assignedUsers) {
            ContestUser cu = new ContestUser();
            cu.setContest(savedContest);
            cu.setUser(user);
            cu.setId(new ContestUserId(savedContest.getContestID(), user.getUserID()));
            cu.setStatus(ContestUserStatus.INACTIVE);
            cu.setExitCount(0);

            contestUsers.add(cu);

        }

        savedContest.getUserAssignedTo().addAll(contestUsers);
        contestRepository.save(savedContest);

        problemService.saveProblemMDFiles(problems);

        try {
            contestScheduler.scheduleContestStart(savedContest.getStartTime().toString(), savedContest.getContestID());
            contestScheduler.scheduleContestEnd(savedContest.getEndTime().toString(), savedContest.getContestID());
        } catch (SchedulerException e) {
            throw new RuntimeException("Error in creating scheduler", e);
        }
    }

    @Transactional
    public void startContest(UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest start, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.ACTIVE);
        contest.getProblemList().forEach(problem -> problem.setVisible(true));
        contestRepository.save(contest);
    }

    @Transactional
    public void endContest(UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);

        if (optional.isEmpty())
            throw new IllegalArgumentException("problem in scheduling contest end, no such id");

        Contest contest = optional.get();
        contest.setStatus(ContestStatus.CLOSED);
        contest.getProblemList().forEach(problem -> problem.setVisible(false));
        contestRepository.save(contest);

        List<UserScoreDto> contestLeaderboard = contestLeaderboardRepo.getLeaderboard(contestId);

        if (contestLeaderboard.size() < 3) {
            contestLeaderboard.forEach(contestant -> {
                Leaderboard lb = leaderBoardService.getLeaderboardDataForUserId(contestant.getUserId());
                if (contestant.getPoints() > 0) {
                    lb.setContestRating(lb.getContestRating() + 10);
                    lb.setOverAllRating(lb.getOverAllRating() + 10);
                    leaderBoardService.setLevel(lb);
                    leaderBoardService.saveRecord(lb);
                }
            });
        } else {
            contestLeaderboard.subList(0, 3).forEach(contestant -> {
                Leaderboard lb = leaderBoardService.getLeaderboardDataForUserId(contestant.getUserId());
                if (contestant.getPoints() > 0) {
                    lb.setContestRating(lb.getContestRating() + 10);
                    lb.setOverAllRating(lb.getOverAllRating() + 10);
                    leaderBoardService.setLevel(lb);
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
                if (contestLeaderboard.get(i + 3).getPoints() > 0) {
                    lb.setContestRating(lb.getContestRating() + pointsDistribution[group]);
                    lb.setOverAllRating(lb.getOverAllRating() + pointsDistribution[group]);
                    leaderBoardService.setLevel(lb);
                    leaderBoardService.saveRecord(lb);
                }
            }
        }

        try {
            getLeaderboardFile(contestId.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Contest ended without creating leaderboard file");
        }
    }

    public List<SubmissionResult> submitContestProblem(UUID userId, ContestSubmissionDto dto) {

        Optional<Contest> optional = contestRepository.findById(dto.getContestId());
        if (optional.isEmpty())
            throw new IllegalArgumentException("No contest for given contestId");

        long submittedTime = Duration.between(optional.get().getStartTime(), LocalDateTime.now()).toMinutes();
        String intervalTime = submittedTime + " minutes";

        if (optional.get().getStatus() != ContestStatus.ACTIVE)
            throw new IllegalArgumentException("Contest is not active");
        if (!contestUserRepository.existsById(new ContestUserId(dto.getContestId(), userId)))
            throw new IllegalArgumentException("You are not allowed to attend this contest");
        //Add optional check
        if (contestUserRepository.findById(new ContestUserId(dto.getContestId(), userId)).orElseThrow().getStatus() != ContestUserStatus.ENTERED)
            throw new IllegalArgumentException("You have exited the contest, you cannot submit problems");

        List<SubmissionResult> result = submissionService.ContestSubmission(userId, optional.get(), dto.getProblemId(), dto.getLanguageId(), dto.getCode());

        Problem problem = problemService.getProblemById(dto.getProblemId());

        ContestSubmission submission = new ContestSubmission();
        submission.setProblem(problem);
        submission.setContest(optional.get());
        submission.setUser(userService.getUserById(userId));
        submission.setContestSubmissionTime(submittedTime);
        submission.setCode(dto.getCode());

        if (result.stream().allMatch(r ->
                r.getStatus().getDescription().equals("Accepted")
        )) submission.setStatus(SubmissionStatus.ACCEPTED);

        else submission.setStatus(SubmissionStatus.FAILED);

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
    public  Map<String, Object>  enterContest(UUID userId, UUID contestId) {
        ContestUser contestUser = contestUserRepo.findByContestAndUser(contestRepository.findById(contestId).get(), userService.getUserById(userId));

        if ((contestUser.getStatus() == ContestUserStatus.EXITED && contestUser.getExitCount() > 3) || contestUser.getStatus() == ContestUserStatus.SUBMITTED)
            throw new IllegalArgumentException("you have already entered the contest, you cannot enter again");

        contestLeaderboardRepo.upsertUserScore(contestId, userId, 0, "0 minutes");
        contestUserRepo.updateStatus(contestId, userId, ContestUserStatus.ENTERED.name());
        Contest contest = getContestById(contestId);

        List<Problem> problems = contest.getProblemList();

        List<EnterContestResDto> resDtoList = new ArrayList<>();

        problems.forEach(problem -> {
            EnterContestResDto dto = new EnterContestResDto();
            dto.setId(problem.getProblemID());
            dto.setName(problem.getProblemName());
            dto.setPoints(problem.getPoints());

            resDtoList.add(dto);
        });

        LocalDateTime contestEndTime = contest.getEndTime();

        Date contestEndDate = Date.from(contestEndTime.atZone(ZoneId.systemDefault()).toInstant());

        String jwt = authService.generateJwt(userService.getUserById(userId).getMailID(), contestEndDate);
        Map<String, Object> result = new HashMap<>();
        result.put("problems", resDtoList);
        result.put("jwt", jwt);

        return result;
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

    public List<ContestResponseDto> getCreatedContest(UUID userId) {
        Optional<List<Contest>> optional = contestRepository.findAllByCreatedBy(userService.getUserById(userId));

        if (optional.isEmpty() || optional.get().isEmpty())
            return Collections.emptyList();

        List<ContestResponseDto> responseDtos = new ArrayList<>();

        optional.get().forEach(contest -> {
            responseDtos.add(contestMapper.toDto(contest));
        });

        return responseDtos;
    }

    public List<ContestResponseDto> getAssignedContest(UUID userId) {
        List<ContestUser> contestUsers = contestUserRepo.findAllByUser(userService.getUserById(userId));

        if (contestUsers.isEmpty())
            return Collections.emptyList();

        List<ContestResponseDto> responseDtos = new ArrayList<>();

        contestUsers.forEach(cu -> {
            responseDtos.add(contestMapper.toDto(cu.getContest()));
        });

        return responseDtos;
    }

    public Resource getLeaderboardFile(String contestId) throws FileNotFoundException {
        Optional<Contest> optional = contestRepository.findById(UUID.fromString(contestId));

        if (optional.isEmpty())
            throw new IllegalArgumentException("No contest exists for this ID: "+ contestId);

        if (optional.get().getStatus() == ContestStatus.CLOSED && excelExporter.fileExists(contestId))
            return new FileSystemResource(excelExporter.getLeaderboardFile(contestId));

        List<UserScoreDto> leaderboardData = contestLeaderboardRepo.getLeaderboard(UUID.fromString(contestId));

        leaderboardData.forEach(dto -> {
            dto.setMailId(userService.getUserById(dto.getUserId()).getMailID());
        });
        try {
            excelExporter.writeToExcel(leaderboardData, contestId);
        }
        catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create leaderboard excel file");
        }

        File file = excelExporter.getLeaderboardFile(contestId);
        if (!file.exists()) {
            throw new RuntimeException("File not found, try again");
        }
        return new FileSystemResource(file);
    }

    public List<SubmissionResult> submitSampleCode(ProblemSubmissionDto dto) {
        try {
            return submissionService.submitCode(dto.getProblemId(), dto.getLanguageId(), dto.getCode(), SubmissionType.SAMPLE);
        }
        catch (Exception e) {
            throw new RuntimeException("Try again, an error occurred");
        }
    }

    public List<Map<String, String>> getAccSubmittedCodes(UUID userId, UUID contestId) {
        Optional<Contest> optional = contestRepository.findById(contestId);
        List<Map<String, String>> res = new ArrayList<>();

        if (optional.isEmpty())
            throw new IllegalArgumentException("no contest exists");
        if (optional.get().getStatus() != ContestStatus.CLOSED)
            throw new IllegalArgumentException("Contest is not closed");

        for (Problem p : optional.get().getProblemList()) {
            String code = contestSubmissionRepo.findFirstAccecptedCodeByUserContestProblem(contestId, userId, p.getProblemID());

            if (code != null) {
                Map<String, String> map = new HashMap<>();
                map.put("problemName", p.getProblemName());
                map.put("code", code);
                res.add(map);
            }
        }

        return res;
    }

    public String getContestProblem(UUID userId, UUID contestId, UUID problemId) {

        User user = userService.getUserById(userId);
        Contest contest = getContestById(contestId);
        ContestUser contestUser = contestUserRepo.findByContestAndUser(contest, user);

        if (contestUser == null)
            throw new IllegalArgumentException("you are not assigned to this contest");

        if (!contestUser.getStatus().equals(ContestUserStatus.ENTERED))
            throw new IllegalArgumentException("You have not entered the contest");

        List<Problem> problems = contest.getProblemList();

        for (Problem p : problems)
            if (p.getProblemID().equals(problemId) && p.isVisible()) {
                return problemService.getMdFile(p.getProblemSlug());
            }
        throw new IllegalArgumentException("No problem exits for this ID: " + problemId);
    }
}
