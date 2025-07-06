package com.pulseband.pulseband.utils;

import javafx.scene.paint.Color;

public class ColorsUtils {
    public static final Color BACKGROUND = Color.web("#000000");
    public static final Color CARD = Color.web("#1A1A1A");
    public static final Color BORDER = Color.web("#333333");

    public static final Color FOREGROUND = Color.web("#FFFFFF");
    public static final Color MUTED_FOREGROUND = Color.web("#B3B3B3");

    public static final Color PRIMARY = Color.web("#65FE08");
    public static final Color PRIMARY_FOREGROUND = Color.web("#143302");
    public static final Color PRIMARY_HOVER = Color.web("#47b206");

    public static final Color DANGEROUS = Color.web("#FF4D4D");
    public static final Color DANGEROUS_FOREGROUND = Color.web("#4C1717");

    public static String toCss(Color color) {
        return color.toString().replace("0x", "#");
    }
}
