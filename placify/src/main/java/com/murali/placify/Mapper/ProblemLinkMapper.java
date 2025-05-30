package com.murali.placify.Mapper;

import com.murali.placify.entity.ProblemLink;
import com.murali.placify.entity.Task;
import com.murali.placify.model.RecommendedProblem;
import com.murali.placify.model.TaskLinkPair;
import com.murali.placify.model.TaskWithProblemLinksDTO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ProblemLinkMapper {
    public void strings2problemLinks(List<String> links, Task task) {
        List<ProblemLink> problemLinks = new ArrayList<>();

        for (String link : links) {
            problemLinks.add(new ProblemLink(link, task));
        }

        task.setProblemLinks(problemLinks);
    }

    public List<Task> bulkStrings2problemLinks(List<TaskLinkPair> pairs) {
        List<Task> tasks = new ArrayList<>();

        pairs.forEach(pair -> {

            Task u = pair.getTask();
            TaskWithProblemLinksDTO v = pair.getDto();

            List<ProblemLink> problemLinks = new ArrayList<>(v.getProblems().size());
            for (RecommendedProblem problem : v.getProblems()){
                ProblemLink problemLink = new ProblemLink(problem.getUrl(), u);
                problemLink.setAcRate(problem.getAcRate());
                problemLink.setDifficulty(problem.getDifficulty());

                problemLinks.add(problemLink);
            }

            u.setProblemLinks(problemLinks);
            tasks.add(u);
        });

        return tasks;
    }
}
