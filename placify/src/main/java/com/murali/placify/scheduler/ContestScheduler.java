package com.murali.placify.scheduler;

import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

@Component
public class ContestScheduler {
    private final Scheduler scheduler;

    public ContestScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleContestStart() {

    }

    public void scheduleContestEnd() {

    }
}
