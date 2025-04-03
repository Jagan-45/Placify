package com.murali.placify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.murali.placify.enums.TaskCron;
import com.murali.placify.enums.TaskScheduledStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks_scheduled")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskScheduled {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_scheduled_id", nullable = false)
    private UUID taskCreatedID;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "schedule_time", nullable = false)
    private LocalTime scheduleTime;

    @Column(name = "cron_expression", nullable = false)
    @JsonIgnore
    private String cronExpression;

    @Column(name = "job_key", length = 255)
    @JsonIgnore
    private String jobKey;

    @Column(name = "trigger_key", length = 255)
    @JsonIgnore
    private String triggerKey;

    @Enumerated(EnumType.STRING)
    private TaskCron repeat;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "taskScheduled", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Task> tasks;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_scheduled_batches",
            joinColumns = @JoinColumn(name = "task_scheduled_id"),
            inverseJoinColumns = @JoinColumn(name = "batch_id")
    )
    private List<Batch> batches;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    @JsonIgnore
    private User createdBy;
}