package com.nv95.fbchatnew.utils;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nv95.fbchatnew.R;

import java.lang.reflect.Field;

/**
 * Created by nv95 on 08.08.16.
 */

public class ThemeUtils {

    public static void paintStatusBar(AppCompatActivity activity, DayNightPalette palette) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(palette.getDarkerColor());
        }
    }

    public static void paintUi(AppCompatActivity activity, DayNightPalette palette) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(palette.getDarkerColor());
        }
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(palette.getDarkColor());
        }
        paintView(activity.getWindow().getDecorView(), palette);
    }

    public static void paintView(View view, DayNightPalette palette) {
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

    public static void setDrawableCompat(ImageView imageView, @DrawableRes int resId, DayNightPalette palette) {
        Drawable d = ContextCompat.getDrawable(imageView.getContext(), resId);
        if (d != null) {
            d.setColorFilter(palette.getContrastColor(), PorterDuff.Mode.SRC_ATOP);
        }
        imageView.setImageDrawable(d);
    }

    public static void paintEditText(EditText editText, DayNightPalette palette) {
        TextInputLayout til = (TextInputLayout) editText.getParent();
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{palette.getAccentColor()}));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{palette.getAccentColor()}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
