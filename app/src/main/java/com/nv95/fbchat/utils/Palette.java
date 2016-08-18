package com.nv95.fbchat.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by nv95 on 08.08.16.
 */

public class Palette {

    protected int mColor;

    protected Palette(int color) {
        mColor = color;
    }

    public int getNormalColor() {
        return mColor;
    }

    public int getDarkColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[1] = 0.78432f;
        hsv[2] = 0.3922f;
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public int getDarkerColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[1] = 0.78432f;
        hsv[2] = 0.2353f;
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public int getLightColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[1] = 0.45f;
        hsv[2] = 1f;
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public int getAccentColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[0] = (hsv[0] + 100) % 360;
        return Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public Palette inverse() {
        mColor = Color.argb(
                Color.alpha(mColor),
                255 - Color.red(mColor),
                255 - Color.green(mColor),
                255 - Color.blue(mColor)
        );
        return this;
    }

    public Palette setAlpha(float alpha) {
        mColor = Color.argb(
                (int) (255 * alpha),
                Color.red(mColor),
                Color.green(mColor),
                Color.blue(mColor)
        );
        return this;
    }

    public int getValue() {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        return (int) hsv[0];
    }

    public void setValue(int value) {
        float[] hsv = new float[3];
        Color.colorToHSV(mColor, hsv);
        hsv[0] = value % 360;
        mColor = Color.HSVToColor(Color.alpha(mColor), hsv);
    }

    public static Palette fromString(@NonNull String what) {
        return new Palette(
                Color.HSVToColor(new float[]{
                        Math.abs(what.hashCode()) % 360,
                        0.6242038f,
                        0.6156863f
                })
        );
    }

    public static Palette fromColor(int color) {
        return new Palette(color);
    }

    public static Palette random() {
        return new Palette(
                Color.HSVToColor(new float[]{
                        new Random().nextInt(360),
                        0.6242038f,
                        0.6156863f
                })
        );
    }

    public static Palette fromValue(int value) {
        return new Palette(
                Color.HSVToColor(new float[]{
                        value % 360,
                        0.6242038f,
                        0.6156863f
                })
        );
    }
}
