package com.murali.placify.controller;

import com.murali.placify.service.TestcaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v0/testcases")
public class TestcaseController {

    private final TestcaseService testcaseService;

    public TestcaseController(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @GetMapping("/{problemSlug}")
    public void getTestcase(@PathVariable("problemSlug") String problemSlug) {
        System.out.println(testcaseService.getSampleTestcases(problemSlug));
    }
}
