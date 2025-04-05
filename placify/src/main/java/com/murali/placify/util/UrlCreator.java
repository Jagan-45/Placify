package com.murali.placify.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlCreator {
    public String createApplicationUrl(HttpServletRequest request) {
        String url = "http://"
                +request.getServerName()+":"
                +5173
                +request.getContextPath();
        System.out.println(url);
        return url;
    }
}
