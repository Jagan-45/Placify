package com.murali.placify.Mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserScoreDto {
    private String username;
    private UUID userId;
    private int points;
    private String timeTaken;

    @JsonIgnore
    private String mailId;

    public UserScoreDto(String username, UUID userId, int points, String timeTaken) {
        this.userId = userId;
        this.timeTaken = timeTaken;
        this.points = points;
        this.username = username;
    }
}
