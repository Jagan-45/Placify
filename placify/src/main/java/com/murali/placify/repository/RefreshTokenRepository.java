package com.murali.placify.repository;

import com.murali.placify.entity.RefreshToken;
import com.murali.placify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String jwt);

    boolean existsByUser(User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.token = :jwt, rt.refreshTokenExpiryTime = :expiryTime WHERE rt.user = :user")
    void updateJwtToken(@Param("expiryTime") LocalDateTime expiryTime,
                        @Param("jwt") String jwt,
                        @Param("user") User user);

    RefreshToken findByUser(User user);
}
