package com.murali.placify.model;

import com.murali.placify.entity.ProblemLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentTaskResDto {
    private String name;
    private UUID id;
    private boolean completed = false;
    private List<ProblemLink> problemLinks;
}
