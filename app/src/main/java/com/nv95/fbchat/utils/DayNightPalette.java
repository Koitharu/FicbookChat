package com.nv95.fbchat.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * Created by nv95 on 14.08.16.
 */

public class DayNightPalette extends Palette {

    private boolean mDark;

    private DayNightPalette(int color, boolean night) {
        super(color);
        mDark = night;
    }

    public boolean isDark() {
        return mDark;
    }

    public void setDark(boolean dark) {
        mDark = dark;
    }

    public int getCompatColor() {
        return mDark ? getDarkColor() : getLightColor();
    }

    public int getInverseColor() {
        return mDark ? getLightColor() : getDarkColor();
    }

    public int getContrastAccentColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[0] = (hsv[0] + DELTA_ACCENT) % 360;
        hsv[1] = mDark ? 0.21373f : 0.71373f;
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public int getAccentColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[0] = (hsv[0] + DELTA_ACCENT) % 360;
        if (mDark) {
            hsv[1] = 0.31373f;
        }
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public int getGrayColor() {
        return Color.argb(
                100,
                isDark() ? 255 : 0,
                isDark() ? 255 : 0,
                isDark() ? 255 : 0
        );
    }

    public int getInverseGrayColor() {
        return Color.argb(
                100,
                isDark() ? 0 : 255,
                isDark() ? 0 : 255,
                isDark() ? 0 : 255
        );
    }

    public int getContrastColor() {
        return Color.argb(
                200,
                isDark() ? 255 : 0,
                isDark() ? 255 : 0,
                isDark() ? 255 : 0
        );
    }

    public static DayNightPalette fromColor(int color, boolean night) {
        return new DayNightPalette(color, night);
    }

    public static DayNightPalette fromValue(int value, boolean night) {
        return new DayNightPalette(
                Color.HSVToColor(new float[]{
                        value % 360,
                        0.6242038f,
                        0.6156863f
                }),
                night
        );
    }

    public static DayNightPalette fromString(@NonNull String what, boolean dark) {
        return new DayNightPalette(
                Color.HSVToColor(new float[]{
                        Math.abs(what.hashCode()) % 360,
                        0.6242038f,
                        0.6156863f
                }),
                dark
        );
    }
}
