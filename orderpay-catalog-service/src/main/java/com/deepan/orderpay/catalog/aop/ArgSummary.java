package com.deepan.orderpay.catalog.aop;

import java.util.Arrays;

final class ArgSummary {

    private ArgSummary() {}

    static String of(Object[] args) {
        return truncate(Arrays.deepToString(args), 200);
    }

    static String truncate(String text, int max) {
        if (text == null) return null;
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}