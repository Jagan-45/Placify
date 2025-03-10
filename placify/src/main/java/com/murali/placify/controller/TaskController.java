package com.murali.placify.controller;

import com.murali.placify.model.TaskWithProblemLinksDTO;
import com.murali.placify.response.APIResponse;
import com.murali.placify.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v0/tasks")
public class TaskController {

    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //TODO: should be accessible only for staff login and change the response as DTO including userID
    @PostMapping("/problems")
    public ResponseEntity<APIResponse> createTaskWithProblemLinks(@RequestBody TaskWithProblemLinksDTO dto) {

        return new ResponseEntity<>(new APIResponse("Task created", taskService.createTaskWithProblemLinks(dto)), HttpStatus.OK);
    }

    //TODO: should be accessible only for staff login and change the response as DTO including userID
    @PostMapping("/problems/bulk")
    public ResponseEntity<APIResponse> createBulkTaskWithProblemLinks(@RequestBody List<TaskWithProblemLinksDTO> dtos) {
        return new ResponseEntity<>(new APIResponse("Tasks created", taskService.createBulkTaskWithProblemLinks(dtos)), HttpStatus.OK);
    }
}
