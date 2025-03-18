package com.murali.placify.Mapper;


import com.murali.placify.entity.Problem;
import com.murali.placify.entity.User;
import com.murali.placify.model.ProblemDTO;

public class ProblemMapper {

    public static Problem mapToProblem(ProblemDTO challenge, User user){
        Problem problem = new Problem();

        problem.setProblemName(challenge.getProblemName());
        problem.setDescription(challenge.getDescription());
        problem.setPoints(challenge.getPoints());
        problem.setCreatedBy(user);
        problem.setProblemSlug(challenge.getProblemSlug());
        problem.setConstrains(challenge.getConstrains());
        problem.setInputFields(challenge.getInputFields());
        problem.setOutputField(challenge.getOutputField());

        return problem;
    }
}
