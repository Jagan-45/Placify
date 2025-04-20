package com.murali.placify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.murali.placify.Mapper.ProblemMapper;
import com.murali.placify.entity.Problem;
import com.murali.placify.exception.*;
import com.murali.placify.model.ProblemDTO;
import com.murali.placify.model.ProblemSubmissionDto;
import com.murali.placify.model.SubmissionResult;
import com.murali.placify.repository.ProblemRepository;
import com.murali.placify.response.ProblemResponse;
import com.murali.placify.response.ProblemSlugResponse;
import com.murali.placify.util.ProblemFileHandler;
import com.murali.placify.util.StructureFileHandler;
import org.aspectj.weaver.ast.Literal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final UserService userService;
    private final StructureFileHandler structureFileHandler;
    private final ProblemFileHandler problemFileHandler;
    private final SubmissionService submissionService;

    private final TestcaseService testcaseService;
    public ProblemService(ProblemRepository problemRepository, UserService userService, StructureFileHandler structureFileHandler, ProblemFileHandler problemFileHandler, SubmissionService submissionService, TestcaseService testcaseService) {
        this.problemRepository = problemRepository;
        this.userService = userService;
        this.structureFileHandler = structureFileHandler;
        this.problemFileHandler = problemFileHandler;
        this.submissionService = submissionService;
        this.testcaseService = testcaseService;
    }

    public List<Problem> createProblems(List<ProblemDTO> dtos) {
        List<Problem> problems = new ArrayList<>();

        for (ProblemDTO dto : dtos)
            problems.add(createProblem(dto));

        return problems;
    }

    public Problem createProblem(ProblemDTO problemDto) throws FileException {
        if (structureFileHandler.saveStructureFile(problemDto)) {
//            if (problemRepository.existsByProblemSlug(problemDto.getProblemSlug()))
//                throw new ProblemAlreadyExistsException("Problem with this slug already exists");

            Problem problem = ProblemMapper.mapToProblem(problemDto, userService.getUserById(UUID.fromString(problemDto.getCreatedBy())));
            problem.setTestcases(testcaseService.createTestcases(problemDto.getTestcases(), problem));

            return problem;
        } else throw new FileException("cannot create structure file");

    }

    public List<Problem> saveProblems(List<Problem> problems) {
        return problemRepository.saveAll(problems);
    }

    public Problem getProblemBySlug(String problemSlug) throws ProblemNotFountException {
        Optional<Problem> optionalProblem = problemRepository.findByProblemSlug(problemSlug);

        if (optionalProblem.isPresent())
            return optionalProblem.get();
        else throw new ProblemNotFountException("Problem doesn't exists");
    }


    public List<ProblemSlugResponse> getProblemsByCreator(long userID) throws UserNotFoundException {
        Optional<List<Problem>> OptionalProblems = problemRepository.findByCreatedBy(userService.getUserById(UUID.fromString("0d06285d-2761-490f-a987-5a8f04a22a84")));

        if (OptionalProblems.isPresent()) {
            List<Problem> problems = new ArrayList<>(OptionalProblems.get());
            List<ProblemSlugResponse> problemSlugRespons = new ArrayList<>(problems.size());
            for (Problem problem : problems)
                problemSlugRespons.add(new ProblemSlugResponse(problem.getProblemName(), problem.getProblemSlug(), problem.getPoints()));

            return problemSlugRespons;
        }
        return new ArrayList<>();
    }

    public ProblemResponse getProblem(String problemSlug) throws ProblemNotFountException {
        Optional<Problem> optionalProblem = problemRepository.findByProblemSlug(problemSlug);

        if (optionalProblem.isPresent()) {
            Problem problem = optionalProblem.get();
            return new ProblemResponse(problem.getProblemName(),
                    problem.getProblemSlug(),
                    problem.getPoints(), problem.getDescription(),
                    problem.getConstrains(), problem.getInputFields(),
                    problem.getOutputField());

        } else throw new ProblemNotFountException("No such problem exists");
    }

    public void saveProblemMD(Problem problem) throws TestcaseException, FileException, ProblemNotFountException {
            problemFileHandler.createProblemMd(problem);
    }

    public void saveProblemMDFiles(List<Problem> problems) {
        problems.forEach(this::saveProblemMD);
    }

    public Problem getProblemById(UUID problemId) {
        Optional<Problem> optionalProblem = problemRepository.findById(problemId);

        if (optionalProblem.isEmpty())
            throw new IllegalArgumentException("No problem exists for this ID");
        return optionalProblem.get();
    }
}
