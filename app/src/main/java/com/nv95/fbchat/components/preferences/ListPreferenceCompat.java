package com.nv95.fbchat.components.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nv95.fbchat.R;

/**
 * Created by nv95 on 20.08.16.
 */

public class ListPreferenceCompat extends ListPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceCompat(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_color, parent, false);
        v.findViewById(R.id.imageView).setVisibility(View.GONE);
        return v;
    }
}
