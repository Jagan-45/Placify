package com.murali.placify.Mapper;

import com.murali.placify.entity.Contest;
import com.murali.placify.model.ContestResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ContestMapper {

    public ContestResponseDto toDto(Contest contest) {
        return new ContestResponseDto(
                contest.getProblemList(),
                contest.getContestID(),
                contest.getContestName(),
                contest.getStartTime(),
                contest.getEndTime()
        );
    }

}
