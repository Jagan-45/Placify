package com.murali.placify.util;

import com.murali.placify.model.ProblemDTO;
import jakarta.validation.constraints.NotBlank;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class StructureFileHandler {

    private final RootPath rootPath;
    private final FolderNameProvider folderNameProvider;

    public StructureFileHandler(RootPath rootPath, FolderNameProvider folderNameProvider) {
        this.rootPath = rootPath;
        this.folderNameProvider = folderNameProvider;
    }


    public boolean saveStructureFile(ProblemDTO problemDTO) {
        String folderName = folderNameProvider.getFolderName(problemDTO.getProblemSlug());
        String ROOT_PATH = rootPath.getRootPath() + "placify" + File.separator + "Problems";
        File file = new File(ROOT_PATH + File.separator + folderName + File.separator + "structure.txt");

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

                    String structure = "problem name: " + problemDTO.getProblemName() + '\n' +
                            "function name: " + getFunctionName(problemDTO.getProblemName()) + '\n' +
                            "input structure:\n" + getInputFields(problemDTO.getInputFields()) +
                            "output structure:\n" + getOutputFields(problemDTO.getOutputField());

                    bw.write(structure);
                    bw.close();
                    fw.close();
                } else return false;

            } catch (IOException e) {
                System.out.println("failed to save structure");
                return false;
            }
            return true;
        } else {
            System.out.println("cannot create folder");
            return false;
        }
    }

    private String getOutputFields(@NotBlank String str) {
        StringBuilder outputFields = new StringBuilder();

        for (String outputField : str.split("\n"))
            outputFields.append("output field: ").append(outputField).append('\n');

        return outputFields.toString();
    }

    private String getFunctionName(String problemName) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(problemName.charAt(0)));
        for (int i = 1; i < problemName.length(); i++) {
            if (problemName.charAt(i - 1) == ' ')
                sb.append(Character.toUpperCase(problemName.charAt(i)));
            else if (problemName.charAt(i) != ' ')
                sb.append(problemName.charAt(i));
        }
        return sb.toString();
    }

    private String getInputFields(String str) {
        StringBuilder inputFields = new StringBuilder();

        for (String inputField : str.split("\n"))
            inputFields.append("input field: ").append(inputField).append('\n');

        return inputFields.toString();
    }
}
