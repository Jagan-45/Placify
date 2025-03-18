package com.murali.placify.service;

import com.murali.placify.Mapper.TestcaseMapper;
import com.murali.placify.entity.Problem;
import com.murali.placify.entity.Testcase;
import com.murali.placify.exception.FileException;
import com.murali.placify.exception.ProblemAlreadyExistsException;
import com.murali.placify.exception.ProblemNotFountException;
import com.murali.placify.exception.TestcaseException;
import com.murali.placify.model.TestcaseDTO;
import com.murali.placify.repository.TestcaseRepository;
import com.murali.placify.response.TestcaseResponse;
import com.murali.placify.response.TestcaseSlugResponse;
import com.murali.placify.util.TestcaseFileHandler;
import com.murali.placify.util.TestcaseFormater;
import org.springframework.stereotype.Service;
import com.murali.placify.repository.ProblemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestcaseService{

    private final TestcaseRepository testcaseRepository;
    private final ProblemRepository problemRepository;
    private final TestcaseFileHandler testcaseFileHandler;
    private final TestcaseFormater testcaseFormater;

    public TestcaseService(TestcaseRepository testcaseRepository, ProblemRepository problemRepository, TestcaseFileHandler testcaseFileHandler, TestcaseFormater testcaseFormater) {
        this.testcaseRepository = testcaseRepository;
        this.problemRepository = problemRepository;
        this.testcaseFileHandler = testcaseFileHandler;
        this.testcaseFormater = testcaseFormater;
    }

    public List<Testcase> createTestcases(List<TestcaseDTO> dtos, Problem problem) {
       List<Testcase> testcases = new ArrayList<>();

       for (TestcaseDTO dto : dtos) {
           testcases.add(createTestcase(dto, problem));
       }

       return testcases;
    }

    public Testcase createTestcase(TestcaseDTO testcaseDto, Problem problem) throws ProblemAlreadyExistsException, FileException, ProblemNotFountException {

        if (testcaseFileHandler.saveTestcase(testcaseDto)) {
            return TestcaseMapper.mapToTestcase(testcaseDto, problem);

        } else throw new FileException("Unable to create testcase");

    }


    public List<TestcaseResponse> getSampleTestcases(String problemSlug) throws TestcaseException, FileException {
        List<Testcase> testcases = testcaseRepository.findSampleByProblemSlug(problemSlug);
        if (testcases.isEmpty())
            throw new TestcaseException("no sample testcases exists");
        else {
            return testcaseFileHandler.getTestcase(testcases);
        }
    }

    public List<TestcaseSlugResponse> getTestcasesByProblemSlug(String problemSlug) throws TestcaseException {
        Optional<List<Testcase>> optionalTestcases = testcaseRepository.findAllByProblemProblemSlug(problemSlug);
        if (optionalTestcases.isPresent()) {

            List<Testcase> testcases = optionalTestcases.get();
            List<TestcaseSlugResponse> responses = new ArrayList<>(testcases.size());

            for (Testcase tc : testcases) {
                responses.add(new TestcaseSlugResponse(tc.getTcName(), tc.isSample()));
            }
            return responses;
        } else throw new TestcaseException("No testcases found for problem slug");
    }

    public List<String> formatedSampleTestcases(String problemSlug) throws TestcaseException, FileException {
        List<String> formatedSampleTestcases = new ArrayList<>();

        for (TestcaseResponse testcaseResponse : getSampleTestcases(problemSlug)) {
            formatedSampleTestcases.add(testcaseFormater.formatTestcase(testcaseResponse));
        }
        return formatedSampleTestcases;
    }

}
