package com.murali.placify.service;

import com.murali.placify.Mapper.ProblemLinkMapper;
import com.murali.placify.Mapper.TaskMapper;
import com.murali.placify.entity.ProblemLink;
import com.murali.placify.entity.Task;
import com.murali.placify.entity.User;
import com.murali.placify.model.TaskLinkPair;
import com.murali.placify.model.TaskWithProblemLinksDTO;
import com.murali.placify.repository.ProblemLinkRepo;
import com.murali.placify.repository.TaskRepo;
import com.murali.placify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final UserRepository userRepository;
    private final ProblemLinkRepo problemLinksRepo;
    private final TaskMapper taskMapper;
    private final ProblemLinkMapper problemLinkMapper;

    public TaskService(TaskRepo taskRepo, UserRepository userRepository, ProblemLinkRepo problemLinksRepo, TaskMapper taskMapper, ProblemLinkMapper problemLinkMapper) {
        this.taskRepo = taskRepo;
        this.userRepository = userRepository;
        this.problemLinksRepo = problemLinksRepo;
        this.taskMapper = taskMapper;
        this.problemLinkMapper = problemLinkMapper;
    }

    @Transactional
    public Task createTaskWithProblemLinks(TaskWithProblemLinksDTO dto) {

        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("No such user exists exception");

        Task task = taskMapper.taskWithProblemLinksDTO2Task(dto, optionalUser.get());

        problemLinkMapper.strings2problemLinks(dto.getLinks(), task);

        //AssignedBy has to be changed in future based on use case
        return taskRepo.save(task);

    }

    @Transactional
    public HashMap<String, Object> createBulkTaskWithProblemLinks(List<TaskWithProblemLinksDTO> dtos) {

        Set<UUID> userIds = dtos.stream().map(TaskWithProblemLinksDTO::getUserId).collect(Collectors.toSet());

        List<User> foundUsers = userRepository.findAllById(userIds);
        HashMap<UUID, User> userMap = new HashMap<>();

        foundUsers.forEach(user -> userMap.put(user.getUserID(), user));

        Set<UUID> foundUserIds = foundUsers.stream().map(User::getUserID).collect(Collectors.toSet());

        userIds.removeAll(foundUserIds);

        List<TaskLinkPair> pairs = taskMapper.bulkTaskWithProblemLinksDTO2Task(dtos, userMap);

        List<Task> savedTasks = taskRepo.saveAll(problemLinkMapper.bulkStrings2problemLinks(pairs));

        HashMap<String,Object> result = new HashMap<>();
        result.put("saved tasks", savedTasks);
        result.put("un-found users", userIds);

        return result;
    }
}
