package com.murali.placify.repository;

import com.murali.placify.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeaderboardRepo extends JpaRepository<Leaderboard, UUID>, JpaSpecificationExecutor<Leaderboard> {


    Leaderboard findByUser_UserID(UUID userId);
}
