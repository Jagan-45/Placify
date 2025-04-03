package com.murali.placify.controller;

import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.model.TaskCreationDto;
import com.murali.placify.model.TaskWithProblemLinksDTO;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/tasks")
public class TaskController {

    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> automateTaskCreation(@RequestBody TaskCreationDto dto) {
        TaskScheduled ts = taskService.createTask(UUID.fromString("50bb1bbd-03df-418b-bf5f-fbff750dde9f"), dto);
        return new ResponseEntity<>(new ApiResponse("Tasks will be scheduled", ts), HttpStatus.OK);
    }

    //TODO: should be accessible only for staff login
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> updateAutomaticTaskCreation(@PathVariable("id") String id, @RequestBody TaskCreationDto dto) {
        TaskScheduled ts = taskService.updateTask(id, dto);
        return new ResponseEntity<>(new ApiResponse("Task settings updated", ts), HttpStatus.OK);

    }

    //TODO: should be accessible only for staff login
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteAutomatedTaskCreation(@PathVariable("id") String id) {
        taskService.deleteTask(id);

        return new ResponseEntity<>(new ApiResponse("Task creation schedule deleted", null), HttpStatus.OK);
    }

    //TODO: should be accessible only for staff login
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse> getAutomatedTaskCreation(@PathVariable("id") String id) {
        List<TaskScheduled> taskScheduledList = taskService.getTaskCreatedBy(id);

        return new ResponseEntity<>(new ApiResponse("Tasks fetched", taskScheduledList), HttpStatus.OK);
    }

    //TODO: should be accessible only for staff login and change the response as DTO including userID
//    @PostMapping("/problems")
//    public ResponseEntity<ApiResponse> createTaskWithProblemLinks(@RequestBody TaskWithProblemLinksDTO dto) {
//
//        return new ResponseEntity<>(new ApiResponse("Task created", taskService.createTaskWithProblemLinks(dto)), HttpStatus.OK);
//    }

//    //TODO: should be accessible only for staff login and change the response as DTO including userID
//    @PostMapping("/problems/bulk")
//    public ResponseEntity<ApiResponse> createBulkTaskWithProblemLinks(@RequestBody List<TaskWithProblemLinksDTO> dtos) {
//        return new ResponseEntity<>(new ApiResponse("Tasks created", taskService.createBulkTaskWithProblemLinks(dtos)), HttpStatus.OK);
//    }
}
