package org.example.springmvc.utils;

public final class SearchUtils {

    private SearchUtils() {
    }

    public static String toWildcardPattern(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase() + "%";
    }
}

