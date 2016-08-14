package com.nv95.fbchatnew;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.nv95.fbchatnew.utils.DayNightPalette;
import com.nv95.fbchatnew.utils.ThemeUtils;

/**
 * Created by nv95 on 13.08.16.
 */

public class BaseAppActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ThemeUtils.paintUi(this, ChatApp.getApplicationPalette());
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ThemeUtils.paintUi(this, ChatApp.getApplicationPalette());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ChatApp.getApplicationPalette().isDark()) {
            setTheme(R.style.AppTheme_Dark);
        }
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        onPaletteChanged(ChatApp.getApplicationPalette());
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if("color".equals(s)) {
            ThemeUtils.paintUi(this, ChatApp.getApplicationPalette());
            onPaletteChanged(ChatApp.getApplicationPalette());
        }
    }

    public void onPaletteChanged(DayNightPalette palette) {

    }

    public void enableTransparentStatusBar(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (color != 0) {
                window.setStatusBarColor(ContextCompat.getColor(this, color));
            }
        }
    }
}
