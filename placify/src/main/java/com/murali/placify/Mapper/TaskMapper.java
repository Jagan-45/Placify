package com.murali.placify.Mapper;

import com.murali.placify.entity.Task;
import com.murali.placify.entity.User;
import com.murali.placify.model.TaskLinkPair;
import com.murali.placify.model.TaskWithProblemLinksDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class TaskMapper {
    public Task taskWithProblemLinksDTO2Task(TaskWithProblemLinksDTO dto, User user) {
        Task task = new Task();

        task.setCreatedAt(LocalDate.now());
        task.setAssignedAt(dto.getAssignAt());
        task.setAssignedTo(user);
        task.setAssignedBy(user);
        return task;
    }

    public List<TaskLinkPair> bulkTaskWithProblemLinksDTO2Task(List<TaskWithProblemLinksDTO> dtoList, HashMap<UUID, User> userMap) {

        List<TaskLinkPair> pairs =new ArrayList<>();

        dtoList.forEach(dto -> {
            if (userMap.containsKey(dto.getUserId())) {
                Task task = new Task();
                task.setCreatedAt(LocalDate.now());
                task.setAssignedAt(dto.getAssignAt());
                task.setAssignedBy(userMap.get(dto.getUserId()));
                task.setAssignedTo(userMap.get(dto.getUserId()));
                pairs.add(new TaskLinkPair(task, dto));
            }
        });

        return pairs;
    }
}
