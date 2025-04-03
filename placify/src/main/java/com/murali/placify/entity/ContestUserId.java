package com.murali.placify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestUserId implements Serializable {

    @Column(name = "contest_id")
    private UUID contestId;

    @Column(name = "user_id")
    private UUID userId;
}
