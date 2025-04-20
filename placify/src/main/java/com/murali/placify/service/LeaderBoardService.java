package com.murali.placify.service;

import com.murali.placify.entity.Leaderboard;
import com.murali.placify.entity.User;
import com.murali.placify.enums.Level;
import com.murali.placify.model.LeaderboardFilterDTO;
import com.murali.placify.repository.LeaderboardRepo;
import com.murali.placify.repository.specification.LeaderboardSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class LeaderBoardService {

    private final LeaderboardRepo leaderBoardRepo;
    private final LeaderboardSpecification leaderboardSpecification;

    public LeaderBoardService(LeaderboardRepo leaderBoardRepo, LeaderboardSpecification leaderboardSpecification) {
        this.leaderBoardRepo = leaderBoardRepo;
        this.leaderboardSpecification = leaderboardSpecification;
    }


    public Page<Leaderboard> getLeaderBoardData(LeaderboardFilterDTO filterDTO, String sortBy, String direction, int page, int size) {
        Specification<Leaderboard> spec = leaderboardSpecification.getAllSpecifications(filterDTO);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return leaderBoardRepo.findAll(spec, pageable);

        //return leaderBoardRepo.findAll(spec);
    }

    public Leaderboard getLeaderboardDataForUserId(UUID userId) {
        return leaderBoardRepo.findByUser_UserID(userId);
    }

    @Transactional
    public void saveRecord(Leaderboard lb) {
        leaderBoardRepo.save(lb);
    }

    public List<Leaderboard> getLeaderBoard() {
        List<Leaderboard> leaderboards = leaderBoardRepo.findAll();
        leaderboards.sort((a, b) -> b.getOverAllRating() - a.getOverAllRating());

        return leaderboards;
    }

    public int getPosition(Leaderboard leaderboard) {
        List<Leaderboard> allData = getLeaderBoard();

        if (!allData.contains(leaderboard))
            throw new IllegalArgumentException("No leaderboard data exists for user: " + leaderboard.getUser().getUsername());

        return allData.indexOf(leaderboard);
    }

    public void setLevel(Leaderboard lb) {
        if (lb.getOverAllRating() >= 150 && lb.getOverAllRating() < 399) lb.setLevel(Level.CODER);
        else if (lb.getOverAllRating() >= 400) lb.setLevel(Level.MASTER);
    }
}
