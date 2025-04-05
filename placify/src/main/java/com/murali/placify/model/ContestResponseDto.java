package com.murali.placify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.entity.Problem;
import com.murali.placify.enums.ContestStatus;
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
    @JsonIgnore
    private List<Problem> problemList;

    private UUID contestID;
    private String contestName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ContestStatus status;
}
