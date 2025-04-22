package com.murali.placify.controller;

import com.murali.placify.entity.Leaderboard;
import com.murali.placify.model.LeaderboardFilterDTO;
import com.murali.placify.service.LeaderBoardService;
import com.murali.placify.util.AppLogger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.murali.placify.response.ApiResponse;

import java.time.Duration;

@RestController
@RequestMapping("api/v0/leaderboard")
public class LeaderboardController {

    private final LeaderBoardService leaderBoardService;

    public LeaderboardController(LeaderBoardService leaderBoardService) {
        this.leaderBoardService = leaderBoardService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getLeaderboard(LeaderboardFilterDTO filterDTO,
                                                            @RequestParam(defaultValue = "overAllRating") String sortBy,
                                                            @RequestParam(defaultValue = "asc") String direction,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        //long st = System.nanoTime();
        Page <Leaderboard> result = leaderBoardService.getLeaderBoardData(filterDTO, sortBy, direction, page, size);
        //long et = System.nanoTime();
        //long tt = et - st;
        //long timeTakenMillis = Duration.ofNanos(tt).toMillis();
        //AppLogger.LOGGER.info("Execution time (in milliseconds): {} ms", timeTakenMillis);

        return new ResponseEntity<>(new ApiResponse("data fetched", result), HttpStatus.OK);
    }
}
