package com.murali.placify.model;

import jdk.dynalink.linker.LinkerServices;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskRecommenderResDto {
    private int studentId;
    private List<String> topics;
    private double acRate;
    private String difficulty;
    private List<RecommendedProblem> problems;
}
