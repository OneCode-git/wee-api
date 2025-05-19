package com.wee.util;

public class UrlValidator {
    private static final String CRLF_REGEX = ".*[\\r\\n].*";

    public static String sanitizeUrl(String url) {
        if (url == null || url.matches(CRLF_REGEX)) {
            throw new IllegalArgumentException("URL contains CRLF sequence");
        }
        return url;
    }
}