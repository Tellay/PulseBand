package com.pulseband.pulseband.utils;

import java.text.Normalizer;

public class TextUtils {
    public static String normalize(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
    }
}
