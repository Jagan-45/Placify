package com.murali.placify.controller;

import com.murali.placify.entity.TaskScheduled;
import com.murali.placify.model.StudentTaskResDto;
import com.murali.placify.model.TaskCreationDto;
import com.murali.placify.model.TaskWithProblemLinksDTO;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.TaskService;
import com.murali.placify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/tasks")
public class TaskController {

    private final TaskService taskService;
    public final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PostMapping()
    public ResponseEntity<ApiResponse> automateTaskCreation(@Valid @RequestBody TaskCreationDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        TaskScheduled ts = taskService.createTask(userId, dto);
        return new ResponseEntity<>(new ApiResponse("Tasks will be scheduled", ts), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> updateAutomaticTaskCreation(@Valid @PathVariable("id") String id, @RequestBody TaskCreationDto dto) {
        TaskScheduled ts = taskService.updateTask(id, dto);
        return new ResponseEntity<>(new ApiResponse("Task settings updated", ts), HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteAutomatedTaskCreation(@PathVariable("id") String id) {
        taskService.deleteTask(id);

        return new ResponseEntity<>(new ApiResponse("Task creation schedule deleted", null), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @GetMapping()
    public ResponseEntity<ApiResponse> getAutomatedTaskCreation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        List<TaskScheduled> taskScheduledList = taskService.getTaskCreatedBy(userId.toString());

        return new ResponseEntity<>(new ApiResponse("Tasks fetched", taskScheduledList), HttpStatus.OK);
    }

    @GetMapping("assigned-task/{date}")
    public ResponseEntity<ApiResponse> getTaskForStudent(@PathVariable("date")LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        StudentTaskResDto result = taskService.getTaskForStudent(userId, date);
        return new ResponseEntity<>(new ApiResponse("", result), HttpStatus.OK);
    }

    @PostMapping("/track-status")
    public ResponseEntity<ApiResponse> trackStatus(@RequestParam("taskId") UUID taskId, @RequestParam("problemId") UUID problemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        StudentTaskResDto result = taskService.trackCompletion(userId, taskId, problemId);
        return new ResponseEntity<>(new ApiResponse("", result), HttpStatus.OK);
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
