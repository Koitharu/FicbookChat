package com.nv95.fbchatnew.components.preferences;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceCategory;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.nv95.fbchatnew.ChatApp;

/**
 * Created by nv95 on 14.08.16.
 */

public class PreferenceCategoryCompat extends PreferenceCategory {
    public PreferenceCategoryCompat(Context context) {
        super(context);
    }

    public PreferenceCategoryCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceCategoryCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PreferenceCategoryCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(ChatApp.getApplicationPalette().getAccentColor());
    }
}
