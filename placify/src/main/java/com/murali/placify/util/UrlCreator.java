package com.murali.placify.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlCreator {
    public String createApplicationUrl(HttpServletRequest request) {
        return "http://"
                +request.getServerName()+":"
                +request.getServerPort()
                +request.getContextPath();
    }
}
