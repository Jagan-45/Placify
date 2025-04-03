package com.murali.placify.model;

import com.murali.placify.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestResponseDto {
    private List<Problem> problemList;
    private UUID contestID;
    private String contestName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
