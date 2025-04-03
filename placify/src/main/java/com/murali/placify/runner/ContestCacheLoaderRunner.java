package com.murali.placify.runner;

import com.murali.placify.cache.ContestCache;
import com.murali.placify.entity.Contest;
import com.murali.placify.entity.ContestSubmission;
import com.murali.placify.enums.ContestStatus;
import com.murali.placify.enums.SubmissionStatus;
import com.murali.placify.repository.ContestRepository;
import com.murali.placify.repository.ContestSubmissionRepo;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContestCacheLoaderRunner implements ApplicationRunner {

    private final ContestSubmissionRepo contestSubmissionRepo;
    private final ContestRepository contestRepository;
    private final ContestCache contestCache;

    public ContestCacheLoaderRunner(ContestSubmissionRepo contestSubmissionRepo, ContestRepository contestRepository, ContestCache contestCache) {
        this.contestSubmissionRepo = contestSubmissionRepo;
        this.contestRepository = contestRepository;
        this.contestCache = contestCache;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Contest> contests = contestRepository.findAllByStatus(ContestStatus.ACTIVE);
        Map<UUID, List<ContestSubmission>> contestSubmissionMap = new HashMap<>();

        contests.forEach(contest -> {
            List<ContestSubmission> cs = contestSubmissionRepo.findAllByContest(contest);
            contestSubmissionMap.put(contest.getContestID(), cs);
        });

        ConcurrentHashMap<UUID, HashMap<UUID, Set<UUID>>> cache = contestCache.getContestUserAcProblemsCache();

        contestSubmissionMap.forEach((u, v) -> {
            HashMap<UUID, Set<UUID>> map = cache.getOrDefault(u, new HashMap<>());

            v.forEach(contestSubmission -> {
                if (contestSubmission.getStatus() == SubmissionStatus.ACCEPTED) {
                    Set<UUID> acProblems = map.getOrDefault(contestSubmission.getUser().getUserID(), new HashSet<>());
                    acProblems.add(contestSubmission.getProblem().getProblemID());

                    map.put(contestSubmission.getUser().getUserID(), acProblems);
                }
            });

            cache.put(u, map);
        });

        System.out.println(cache);

    }

}
