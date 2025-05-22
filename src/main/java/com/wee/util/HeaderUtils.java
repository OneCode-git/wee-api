package com.wee.util;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

public class HeaderUtils {
    private static final Pattern INVALID_HEADER_CHARS = Pattern.compile("[\r\n]");
    
    public static void setHeaderSafely(HttpServletResponse response, String name, String value) {
        if (name == null || value == null || INVALID_HEADER_CHARS.matcher(value).find()) {
            throw new IllegalArgumentException("Invalid header value detected");
        }
        response.setHeader(name, value);
    }
}