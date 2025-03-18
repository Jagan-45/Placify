package com.murali.placify.util;

import com.murali.placify.entity.Testcase;
import com.murali.placify.exception.FileException;
import com.murali.placify.model.TestcaseDTO;
import com.murali.placify.response.TestcaseResponse;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestcaseFileHandler {

    private final RootPath rootPath;

    public TestcaseFileHandler(RootPath rootPath, FolderNameProvider folderNameProvider) {
        this.rootPath = rootPath;
        this.folderNameProvider = folderNameProvider;
    }
    private final FolderNameProvider folderNameProvider;

    public boolean saveTestcase(TestcaseDTO tc) {
        String PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";

        String folderName = folderNameProvider.getFolderName(tc.getProblemSlug());

        if (createFolder(folderName)) {
            String INPUT_FILE_PATH = PATH + File.separator + folderName + File.separator + "inputs" + File.separator;
            String OUTPUT_FILE_PATH = PATH + File.separator + folderName + File.separator + "outputs" + File.separator;


            //create file with tcName
            File inFile = new File(INPUT_FILE_PATH + tc.getTcName() + ".txt");
            File outFile = new File(OUTPUT_FILE_PATH + tc.getTcName() + ".txt");

            boolean filesCreated = false;

            try {
                if (!inFile.exists() && !outFile.exists()) {
                    if (inFile.createNewFile() && outFile.createNewFile())
                        filesCreated = true;
                } else
                    filesCreated = true;

                if (filesCreated) {

                    FileWriter inFw = new FileWriter(inFile);
                    BufferedWriter inWriter = new BufferedWriter(inFw);
                    inWriter.write(tc.getInputFields());

                    FileWriter outFw = new FileWriter(outFile);
                    BufferedWriter outWriter = new BufferedWriter(outFw);
                    outWriter.write(tc.getOutputField());

                    inWriter.close();
                    inFw.close();
                    outWriter.close();
                    outFw.close();
                } else {
                    System.out.println("files creation failed");
                    return false;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }

            return true;
        } else {
            System.out.println("folder creation failed");
            return false;
        }

    }


    private boolean createFolder(String folderName) {
        String PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";

        File inputs = new File(PATH + File.separator + folderName + File.separator + "inputs");
        File outputs = new File(PATH + File.separator + folderName + File.separator + "outputs");

        return (inputs.exists() && outputs.exists()) || (inputs.mkdirs() && outputs.mkdirs());
    }


    public List<TestcaseResponse> getTestcase(List<Testcase> testcases) throws FileException {

        String PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";

        List<TestcaseResponse> testcaseRespons = new ArrayList<>();

        for (Testcase tc : testcases) {
            StringBuffer input = new StringBuffer();
            StringBuffer output = new StringBuffer();

            String folderName = tc.getProblem().getProblemSlug();

            String INPUT_FILE_PATH = PATH + File.separator + folderName + File.separator + "inputs" + File.separator;
            String OUTPUT_FILE_PATH = PATH + File.separator + folderName + File.separator + "outputs" + File.separator;

            File inFile = new File(INPUT_FILE_PATH + tc.getTcName() + ".txt");
            File outFile = new File(OUTPUT_FILE_PATH + tc.getTcName() + ".txt");

            try {


                FileReader inputFileReader = new FileReader(inFile);
                FileReader outputFileReader = new FileReader(outFile);

                BufferedReader inputBufferedReader = new BufferedReader(inputFileReader);
                BufferedReader outputBufferedReader = new BufferedReader(outputFileReader);

                while (inputBufferedReader.ready()) {
                    input.append(inputBufferedReader.readLine()).append('\n');
                }
                while (outputBufferedReader.ready()) {
                    output.append(outputBufferedReader.readLine()).append('\n');
                }

                TestcaseResponse response = new TestcaseResponse();
                response.setTcName(tc.getTcName());
                response.setExplanation(tc.getExplanation());
                response.setInputFields(tc.getProblem().getInputFields());
                response.setOutputFields(tc.getProblem().getOutputField());
                response.setInput(input.toString());
                response.setOutput(output.toString());

                testcaseRespons.add(response);
            } catch (IOException e) {
                throw new FileException("Cannot read testcase files");
            }


        }
        return testcaseRespons;
    }
}
