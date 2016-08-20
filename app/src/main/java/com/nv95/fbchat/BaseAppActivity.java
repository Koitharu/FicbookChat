package com.nv95.fbchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.nv95.fbchat.utils.DayNightPalette;
import com.nv95.fbchat.utils.ThemeUtils;

/**
 * Created by nv95 on 13.08.16.
 */

public class BaseAppActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean mKeepScreen = false;

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setKeepScreenOn(prefs.getBoolean("keepscreen", false));
        prefs.registerOnSharedPreferenceChangeListener(this);
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
        } else if ("keepscreen".equals(s)) {
            setKeepScreenOn(sharedPreferences.getBoolean("keepscreen", false));
        }
    }

    public void onPaletteChanged(DayNightPalette palette) {

    }

    void setSubtitle(CharSequence scq) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(scq);
        }
    }

    void setSubtitle(@StringRes int resId) {
        setSubtitle(getString(resId));
    }

    private void setKeepScreenOn(boolean keep) {
        if (keep != mKeepScreen) {
            mKeepScreen = keep;
            if (keep) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }
}
