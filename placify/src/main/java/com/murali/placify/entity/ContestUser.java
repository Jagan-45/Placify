package com.murali.placify.entity;


import com.murali.placify.enums.ContestUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "contest_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContestUser {

    @EmbeddedId
    private ContestUserId id;

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContestUserStatus status;

    @Column(name = "exit_count")
    private int exitCount;

}