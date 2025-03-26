package com.murali.placify.util;

import com.murali.placify.entity.Problem;
import com.murali.placify.exception.FileException;
import com.murali.placify.exception.TestcaseException;
import com.murali.placify.service.TestcaseService;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class ProblemFileHandler {

    private final RootPath rootPath;
    private final TestcaseService testcaseService;
    private final FolderNameProvider folderNameProvider;

    public ProblemFileHandler(RootPath rootPath, TestcaseService testcaseService, TestcaseService testcaseService1, FolderNameProvider folderNameProvider) {
        this.rootPath = rootPath;
        this.testcaseService = testcaseService1;
        this.folderNameProvider = folderNameProvider;
    }

    public boolean createProblemMd(Problem problem) throws TestcaseException, FileException {
        String folderName = folderNameProvider.getFolderName(problem.getProblemSlug());
        String ROOT_PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";
        File file = new File(ROOT_PATH + File.separator + folderName + File.separator + "problem.md");

        boolean filesCreated = false;

        if (rootPath.createFolder(folderName)) {
            try {
                if (!file.exists()) {
                    if (file.createNewFile())
                        filesCreated = true;
                } else filesCreated = true;

                if (filesCreated) {
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);

                    StringBuffer formatedSampleTestcase = new StringBuffer();
                    List<String> formated = testcaseService.formatedSampleTestcases(problem.getProblemSlug());
                    for (int i = 0; i < formated.size(); i++) {
                        formatedSampleTestcase.append("\n#### Testcase ").append(i + 1).append('\n').append(formated.get(i));
                    }

                    String problemMd = "## " + problem.getProblemName()
                            + "\n\n### Description\n" + problem.getDescription()
                            + "\n\n### Testcase\n" + formatedSampleTestcase + "\n\n### Constraints\n" + problem.getConstrains();

                    bw.write(problemMd);
                    bw.close();
                    fw.close();
                } else return false;

            } catch (IOException e) {
                System.out.println("failed to save problem md");
                return false;
            }
            return true;
        } else {
            System.out.println("cannot create folder");
            return false;
        }
    }
}
