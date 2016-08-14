package com.nv95.fbchatnew.utils;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.nv95.fbchatnew.R;

/**
 * Created by nv95 on 08.08.16.
 */

public class ThemeUtils {

    public static void paintStatusBar(AppCompatActivity activity, Palette palette) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(palette.getDarkerColor());
        }
    }

    public static void paintUi(AppCompatActivity activity, Palette palette) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(palette.getDarkerColor());
        }
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(palette.getDarkColor());
        }
        paintView(activity.getWindow().getDecorView(), palette);
    }

    public static void paintView(View view, Palette palette) {
        if (view instanceof ProgressBar) {
            setProgressBarTint((ProgressBar) view, palette.getAccentColor());
        } else if (view instanceof EditText) {
            view.getBackground().setColorFilter(palette.getAccentColor(), PorterDuff.Mode.SRC_ATOP);
        } else if (view instanceof FloatingActionButton) {
            ((FloatingActionButton) view).setBackgroundTintList(ColorStateList.valueOf(palette.getAccentColor()));
        } else if (view instanceof ViewGroup) {
            for (int i = ((ViewGroup) view).getChildCount();i>=0;i--) {
                paintView(((ViewGroup) view).getChildAt(i), palette);
            }
        }
    }

    private static void setProgressBarTint(ProgressBar progressBar, int color) {
        Drawable d = progressBar.getIndeterminateDrawable();
        if (d != null) {
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        d = progressBar.getProgressDrawable();
        if (d != null) {
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
