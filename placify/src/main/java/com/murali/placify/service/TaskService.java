package com.murali.placify.service;

import com.murali.placify.Mapper.ProblemLinkMapper;
import com.murali.placify.Mapper.TaskMapper;
import com.murali.placify.entity.Batch;
import com.murali.placify.entity.Task;
import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.entity.User;
import com.murali.placify.model.*;
import com.murali.placify.repository.ProblemLinkRepo;
import com.murali.placify.repository.TaskRepo;
import com.murali.placify.repository.TaskScheduledRepo;
import com.murali.placify.repository.UserRepository;
import com.murali.placify.scheduler.TaskScheduler;
import com.murali.placify.util.GlobalHelper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final UserRepository userRepository;
    private final ProblemLinkRepo problemLinksRepo;
    private final TaskMapper taskMapper;
    private final ProblemLinkMapper problemLinkMapper;
    private final UserService userService;
    private final LeaderBoardService leaderBoardService;
    private final WebClient webClient;
    private final GlobalHelper globalHelper;
    private final TaskScheduler taskScheduler;
    private final TaskScheduledRepo taskScheduledRepo;
    private final BatchService batchService;

    public TaskService(TaskRepo taskRepo, UserRepository userRepository, ProblemLinkRepo problemLinksRepo, TaskMapper taskMapper, ProblemLinkMapper problemLinkMapper, UserService userService, LeaderBoardService leaderBoardService, WebClient webClient, GlobalHelper globalHelper, TaskScheduler taskScheduler, TaskScheduledRepo taskScheduledRepo, BatchService batchService) {
        this.taskRepo = taskRepo;
        this.userRepository = userRepository;
        this.problemLinksRepo = problemLinksRepo;
        this.taskMapper = taskMapper;
        this.problemLinkMapper = problemLinkMapper;
        this.userService = userService;
        this.leaderBoardService = leaderBoardService;
        this.webClient = webClient;
        this.globalHelper = globalHelper;
        this.taskScheduler = taskScheduler;
        this.taskScheduledRepo = taskScheduledRepo;
        this.batchService = batchService;
    }

    public TaskScheduled createTask(UUID createdBy, TaskCreationDto dto) {
        TaskScheduled ts = new TaskScheduled();
        ts.setCreatedAt(OffsetDateTime.now());
        ts.setUpdatedAt(OffsetDateTime.now());
        ts.setFromDate(dto.getFrom());
        ts.setToDate(dto.getTo());
        ts.setRepeat(dto.getRepeat());
        ts.setCreatedBy(userService.getUserById(createdBy));
        ts.setScheduleTime(dto.getAssignAtTime());

        List<Batch> batches = batchService.findBatchesByName(dto.getBatches());

        batches.forEach(b -> {b.getTasksScheduled().add(ts);});
        ts.setBatches(batches);

        return taskScheduler.scheduleTask(ts, createdBy, dto);
    }

    public List<Task> createAutomatedTasks(UUID createdBy, TaskCreationDto dto) {

        List<User> assignToUsers = getUnassignedUsers(userService.getUserByBatch(dto.getBatches()));

        List<TaskRecommenderResDto> responses = new ArrayList<>();

        for (int i = 0; i < assignToUsers.size(); i++) {
            TaskRecommenderResDto res = callTaskRecommender(new TaskRecommenderDto(i, leaderBoardService.getLeaderboardDataForUserId(assignToUsers.get(i).getUserID()).getOverAllRating() / 10));
            responses.add(res);
        }

        List<TaskWithProblemLinksDTO> bulkTaskDTOs = new ArrayList<>();

        for (TaskRecommenderResDto res : responses) {
            User user = assignToUsers.get(res.getStudentId());
            List<RecommendedProblem> problems = globalHelper.getTwoRandomElements(res.getProblems());

            TaskWithProblemLinksDTO task = new TaskWithProblemLinksDTO();
            task.setUserId(user.getUserID());
            task.setAssignAt(LocalDate.now());
            task.setProblems(problems);

            bulkTaskDTOs.add(task);
        }

        return createBulkTaskWithProblemLinks(createdBy, bulkTaskDTOs);

    }

    private List<User> getUnassignedUsers(List<User> allUsers) {
        return allUsers.stream()
                .filter(user -> user.getAssignedTasks().stream()
                        .noneMatch(task -> task.getAssignedAt().isEqual(LocalDate.now()))
                )
                .collect(Collectors.toList());
    }

    public TaskRecommenderResDto callTaskRecommender(TaskRecommenderDto dto) {

        return webClient.post()
                .uri("http://localhost:8000/recommend-problems")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .retrieve()
                .bodyToMono(TaskRecommenderResDto.class)
                .block();
    }

//    @Transactional
//    public Task createTaskWithProblemLinks(TaskWithProblemLinksDTO dto) {
//
//        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
//        if (optionalUser.isEmpty())
//            throw new IllegalArgumentException("No such user exists exception");
//
//        Task task = taskMapper.taskWithProblemLinksDTO2Task(dto, optionalUser.get());
//
//        problemLinkMapper.strings2problemLinks(dto.getLinks(), task);
//
//        //AssignedBy has to be changed in future based on use case
//        return taskRepo.save(task);
//
//    }


    public List<Task> createBulkTaskWithProblemLinks(UUID createdBy, List<TaskWithProblemLinksDTO> dtos) {

        Set<UUID> userIds = dtos.stream().map(TaskWithProblemLinksDTO::getUserId).collect(Collectors.toSet());

        List<User> foundUsers = userRepository.findAllById(userIds);
        HashMap<UUID, User> userMap = new HashMap<>();

        foundUsers.forEach(user -> userMap.put(user.getUserID(), user));

        Set<UUID> foundUserIds = foundUsers.stream().map(User::getUserID).collect(Collectors.toSet());

        userIds.removeAll(foundUserIds);

        List<TaskLinkPair> pairs = taskMapper.bulkTaskWithProblemLinksDTO2Task(userService.getUserById(createdBy), dtos, userMap);
        List<Task> tasks = problemLinkMapper.bulkStrings2problemLinks(pairs);

        //List<Task> savedTasks = taskRepo.saveAllAndFlush();

//        HashMap<String, Object> result = new HashMap<>();
//        result.put("saved tasks", savedTasks);
//        result.put("un-found users", userIds);

        return tasks;
    }

    @Transactional
    public void saveAssociatedTasks(TaskScheduled ts, List<Task> tasksCreated, TaskCreationDto dto, String jobKey, String triggerKey, String cronExp, UUID createdBy) {

        for (Task task : tasksCreated) {
            task.setTaskScheduled(ts);
        }
        ts.setTasks(tasksCreated);

        taskScheduledRepo.saveAndFlush(ts);
    }

    public List<TaskScheduled> getAllScheduledTasks() {
        return taskScheduledRepo.findAll();
    }

    public TaskScheduled save(TaskScheduled ts) {
        return taskScheduledRepo.save(ts);
    }

    public TaskScheduled updateTask(String id, TaskCreationDto dto) {
        Optional<TaskScheduled> optional = taskScheduledRepo.findById(UUID.fromString(id));

        if (optional.isEmpty())
            throw new IllegalArgumentException("No task exists for ID: " + id);

        TaskScheduled ts = optional.get();

        ts.setUpdatedAt(OffsetDateTime.now());
        ts.setBatches(batchService.findBatchesByName(dto.getBatches()));
        ts.setRepeat(dto.getRepeat());

        return taskScheduler.updateTaskSchedule(ts, dto);
    }

    public void deleteTask(String id) {
        Optional<TaskScheduled> optional = taskScheduledRepo.findById(UUID.fromString(id));

        if (optional.isEmpty())
            throw new IllegalArgumentException("No task exists for ID: " + id);

        taskScheduler.deleteTask(optional.get());

        taskScheduledRepo.deleteById(UUID.fromString(id));

    }

    public List<TaskScheduled> getTaskCreatedBy(String id) {

        return taskScheduledRepo.findAllByCreatedBy(userService.getUserById(UUID.fromString(id)));
    }
}
