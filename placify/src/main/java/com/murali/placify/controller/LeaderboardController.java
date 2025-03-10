package com.murali.placify.controller;

import com.murali.placify.entity.Leaderboard;
import com.murali.placify.model.LeaderboardFilterDTO;
import com.murali.placify.response.APIResponse;
import com.murali.placify.service.LeaderBoardService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v0/leaderboard")
public class LeaderboardController {

    private final LeaderBoardService leaderBoardService;

    public LeaderboardController(LeaderBoardService leaderBoardService) {
        this.leaderBoardService = leaderBoardService;
    }

    @GetMapping()
    public ResponseEntity<APIResponse> getLeaderboard(LeaderboardFilterDTO filterDTO,
                                                            @RequestParam(defaultValue = "overAllRating") String sortBy,
                                                            @RequestParam(defaultValue = "asc") String direction,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {

        Page <Leaderboard> result = leaderBoardService.getLeaderBoardData(filterDTO, sortBy, direction, page, size);
        System.out.println(result);
        return new ResponseEntity<>(new APIResponse("data fetched", result), HttpStatus.OK);
    }
}
