package com.murali.placify.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@NoArgsConstructor
public class RootPath {

    public String getRootPath() {
        String completePath = System.getProperty("user.dir");
        String[] arr = completePath.split(File.separator.equals("\\") ? "\\\\" : File.separator);
        StringBuilder rootPath = new StringBuilder();

        for (String s : arr) {
            if (s.equals("placify"))
                break;
            else
                rootPath.append(s).append(File.separator);
        }
        return rootPath.toString();
    }

    public boolean createFolder(String folderName) {
        String ROOT_PATH = getRootPath() + "placify" + File.separator + "Problems";
//        System.out.println(ROOT_PATH);
//        System.out.println(ROOT_PATH + File.separator + folderName);
        File problemFolder = new File(ROOT_PATH + File.separator + folderName);

        return problemFolder.exists() || problemFolder.mkdir();
    }
}
