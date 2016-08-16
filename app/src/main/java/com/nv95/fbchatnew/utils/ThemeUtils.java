package com.nv95.fbchatnew.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nv95.fbchatnew.ChatApp;
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
        } else if (view instanceof AppBarLayout) {
            view.setBackgroundColor(palette.getDarkColor());
        } else if (view instanceof CollapsingToolbarLayout) {
            int c = palette.getDarkColor();
            view.setBackgroundColor(c);
            ((CollapsingToolbarLayout)view).setContentScrimColor(c);
            ((CollapsingToolbarLayout)view).setStatusBarScrimColor(c);
        } else if (view instanceof FloatingActionButton) {
            //noinspection RedundantCast
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
        imageView.setImageDrawable(getThemedDrawable(imageView.getContext(), resId, palette.getContrastColor()));
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

    public static Drawable getThemedDrawable(Context context, @DrawableRes int drawableId, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return drawable;
    }

    public static class DialogPainter implements DialogInterface.OnShowListener {

        @Override
        public void onShow(DialogInterface dialogInterface) {
            if (dialogInterface instanceof AlertDialog) {
                DayNightPalette palette = ChatApp.getApplicationPalette();
                Button b = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                if (b != null) {
                    b.setTextColor(palette.getAccentColor());
                }
                b = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                if (b != null) {
                    b.setTextColor(palette.getAccentColor());
                }
                b = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL);
                if (b != null) {
                    b.setTextColor(palette.getAccentColor());
                }
            }
        }
    }
}
