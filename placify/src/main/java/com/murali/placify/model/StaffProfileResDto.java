package com.murali.placify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaffProfileResDto implements ProfileResDto {
    private String dept;
    private String username;
    private String emailId;
    private int batchesCreated;
    private int tasksCreated;
    private int contestCreated;
    private int totalStudentsInAllBatches;
}
