package com.murali.placify.model;

import com.murali.placify.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeaderboardFilterDTO {
    private String department;
    private String academicBatch;
    private Integer taskStreakMin;
    private Integer taskStreakMax;
    private Integer contestRatingMin;
    private Integer contestRatingMax;
    private Level level;
    private int overallRatingMin;
    private int overallRatingMax;
}
