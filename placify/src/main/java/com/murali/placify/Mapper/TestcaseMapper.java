package com.murali.placify.Mapper;


import com.murali.placify.entity.Problem;
import com.murali.placify.entity.Testcase;
import com.murali.placify.model.TestcaseDTO;

public class TestcaseMapper {

    public static Testcase mapToTestcase(TestcaseDTO testcaseDTO, Problem problem){

        Testcase testcase = new Testcase();
        testcase.setProblem(problem);
        testcase.setTcName(testcaseDTO.getTcName());
        testcase.setSample(testcaseDTO.isSample());
        testcase.setExplanation(testcase.getExplanation());

        return testcase;
    }
}
