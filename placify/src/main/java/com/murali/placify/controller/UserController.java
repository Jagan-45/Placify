package com.murali.placify.controller;

import com.murali.placify.model.ProfileResDto;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v0/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UUID userId = userService.getUserIdByEmail(username);

        return new ResponseEntity<>(new ApiResponse("profile data", userService.getProfileInfo(userId)), HttpStatus.OK);
    }
}
