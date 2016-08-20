package com.nv95.fbchat.components.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nv95.fbchat.BuildConfig;
import com.nv95.fbchat.R;

/**
 * Created by nv95 on 20.08.16.
 */

public class AboutPreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AboutPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AboutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_about, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ((TextView)view.findViewById(android.R.id.text1))
                .setText(view.getContext().getString(R.string.about, BuildConfig.VERSION_NAME));
    }
}
