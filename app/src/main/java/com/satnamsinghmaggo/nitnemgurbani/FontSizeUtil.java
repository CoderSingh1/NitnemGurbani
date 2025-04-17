package com.satnamsinghmaggo.nitnemgurbani;

import android.util.TypedValue;
import android.widget.TextView;

public class FontSizeUtil {
    public enum FontSizeLevel {
        SMALL, MEDIUM, LARGE
    }

    public static void applyFontSize(FontSizeLevel level, TextView... views) {
        float sizeSp;
        switch (level) {
            case SMALL:
                sizeSp = 14f;
                break;
            case MEDIUM:
                sizeSp = 18f;
                break;
            case LARGE:
                sizeSp = 22f;
                break;
            default:
                sizeSp = 18f;
        }

        for (TextView view : views) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        }
    }
}