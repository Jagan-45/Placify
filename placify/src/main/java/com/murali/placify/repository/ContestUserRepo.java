package com.murali.placify.repository;

import com.murali.placify.entity.Contest;
import com.murali.placify.entity.ContestUser;
import com.murali.placify.entity.ContestUserId;
import com.murali.placify.entity.User;
import com.murali.placify.enums.ContestUserStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContestUserRepo extends CrudRepository<ContestUser, ContestUserId> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE contest_user SET status = CAST(:status AS VARCHAR) WHERE contest_id = :contestId AND user_id = :userId", nativeQuery = true)
    int updateStatus(@Param("contestId") UUID contestId, @Param("userId") UUID userId, @Param("status") String status);


    ContestUser findByUser(User userById);

    ContestUser findByContestAndUser(Contest byId, User userById);

    List<ContestUser> findAllByUser(User userById);
}