package com.murali.placify.util;

import com.murali.placify.entity.Problem;
import com.murali.placify.exception.FileException;
import com.murali.placify.exception.TestcaseException;
import com.murali.placify.service.TestcaseService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                    List<String> formated = testcaseService.formatedSampleTestcases(problem.getProblemID());
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

    public String getMdFile(String problemSlug) {
        String folderName = folderNameProvider.getFolderName(problemSlug);
        String ROOT_PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";
        File file = new File(ROOT_PATH + File.separator + folderName + File.separator + "problem.md");

        if (!file.exists())
            throw new RuntimeException("No MD file exists; this problem cannot be opened");

        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Problem cannot be opened, error in reading MD file", e);
        }

    }
}
