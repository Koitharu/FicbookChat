package com.nv95.fbchat.components.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nv95.fbchat.R;

/**
 * Created by nv95 on 23.09.16.
 */

public class PreferenceCompat extends Preference {

    public PreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
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
