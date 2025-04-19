package com.murali.placify.util;

import com.murali.placify.response.TestcaseResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TestcaseFormater {

    public String formatTestcase(TestcaseResponse testcaseResponse) {
        StringBuffer formated = new StringBuffer();

        String[] inputFields = testcaseResponse.getInputFields().split("\n");

        String[] inputs = testcaseResponse.getInput().split("\n");
        System.out.println(Arrays.toString(inputs));
        int i = 0;
        formated.append("\n##### Input\n").append("```\n");

        formated.append(testcaseResponse.getInput());
//        for (String inputField : inputFields) {
//            String listArrayRegex = ".*(?:list<[^>]+>|\\[\\])(.*)";
//            Pattern listArrayPattern = Pattern.compile(listArrayRegex);
//            Matcher listArrayMatcher = listArrayPattern.matcher(inputField);
//
//            String twoDRegex = ".*(?:list<list<[^>]+>>|\\\\[\\\\]\\\\[\\\\])(.*)";
//            Pattern twoDPattern = Pattern.compile(twoDRegex);
//            Matcher twoDMatcher = twoDPattern.matcher(inputField);
//
//            String primitiveRegex = ".*<[^>]+>(.*)";
//            Pattern primitivePattern = Pattern.compile(primitiveRegex);
//            Matcher primitiveMatcher = primitivePattern.matcher(inputField);
//
//            if (twoDMatcher.matches()) {
//                String[] innerLists = inputs[i].split("#SEP#");
//                formated.append(twoDMatcher.group(1)).append(" = [");
//                for (String inner : innerLists) {
//                    formated.append('[').append(inner.replaceAll("\t", " ")).append("] ");
//                }
//                formated.append("]");
//            } else if (listArrayMatcher.matches()) {
//                formated.append(listArrayMatcher.group(1)).append(" = [").append(inputs[i].replaceAll("\t", " ")).append(']');
//            } else if (primitiveMatcher.matches()) {
//
//                formated.append(primitiveMatcher.group(1)).append(" = ").append(inputs[i]);
//            } else System.out.println("ERROR ON FORMATING....");
//
//            formated.append("\n");
//            i++;
//        }
        formated.append("```\n");
        formated.append("##### Output\n");
        formated.append("\n").append("```\n").append(testcaseResponse.getOutput()).append('\n');
        formated.append("```\n");

        return formated.toString();
    }
}
