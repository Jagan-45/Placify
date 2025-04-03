package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskRecommenderDto {
    private int studentId;
    private int scoreLevel;
    private static String prompt = "recommend some leetcode problems";
}
