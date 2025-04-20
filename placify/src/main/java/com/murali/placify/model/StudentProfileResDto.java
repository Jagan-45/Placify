package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentProfileResDto implements ProfileResDto {
    private String username;
    private String mailId;
    private String dept;
    private int year;
    private int rating;
    private int globalRank;
    private int contestParticipated;
    private int problemSolved;
    private int taskStreak;
}
