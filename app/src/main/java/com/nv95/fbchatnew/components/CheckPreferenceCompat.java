package com.nv95.fbchatnew.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;

/**
 * Created by nv95 on 14.08.16.
 */

public class CheckPreferenceCompat extends CheckBoxPreference {

    public CheckPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CheckPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CheckPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckPreferenceCompat(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_check, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        SwitchCompat sc = ((SwitchCompat)view.findViewById(android.R.id.checkbox));
        sc.getThumbDrawable().setColorFilter(ChatApp.getApplicationPalette().getAccentColor(), PorterDuff.Mode.MULTIPLY);
        //sc.getTrackDrawable().setColorFilter(ChatApp.getApplicationPalette().getDarkColor(), PorterDuff.Mode.MULTIPLY);
    }
}
