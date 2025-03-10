package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

//need to verify date is future
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskWithProblemLinksDTO {
    private List<String> links;
    private UUID userId;
    private LocalDate assignAt;
}
