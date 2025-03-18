package com.murali.placify.repository;

import com.murali.placify.entity.User;
import com.murali.placify.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    boolean existsByUser(User user);

    VerificationToken findByUser(User user);

    Optional<VerificationToken> findByToken(String token);

    @Query("""
            SELECT user FROM VerificationToken v WHERE v.token = :token
            """)
    Optional<User> findUserByToken(String token);
}
