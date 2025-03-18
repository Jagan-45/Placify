package com.murali.placify.util;

import org.springframework.stereotype.Component;

@Component
public class FolderNameProvider {

    public String getFolderName(String slug) {
        return slug + "placify_FP_jmm_25";
    }

}
