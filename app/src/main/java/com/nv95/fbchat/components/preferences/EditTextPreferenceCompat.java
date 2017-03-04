package com.nv95.fbchat.components.preferences;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nv95.fbchat.R;

/**
 * Created by unravel22 on 04.03.17.
 */

public class EditTextPreferenceCompat extends EditTextPreference {
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditTextPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    public EditTextPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    public EditTextPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public EditTextPreferenceCompat(Context context) {
        super(context);
    }
    
    @SuppressLint("MissingSuperCall")
    @Override
    protected View onCreateView(ViewGroup parent) {
        return LayoutInflater.from(
                getContext())
                .inflate(R.layout.pref_color, parent, false);
    }
    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setVisibility(View.GONE);
    }
}
