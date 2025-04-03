package com.murali.placify.Mapper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserScoreDto {
    private UUID userId;
    private int points;
    private String timeTaken;
}
