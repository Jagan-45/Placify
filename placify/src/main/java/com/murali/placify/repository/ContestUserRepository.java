package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.ContestUser;
import com.murali.placify.entity.ContestUserId;
import com.murali.placify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContestUserRepository extends JpaRepository<ContestUser, ContestUserId> {

    boolean existsByContestContestIDAndUserUserID(UUID contestId, UUID userId);
}
