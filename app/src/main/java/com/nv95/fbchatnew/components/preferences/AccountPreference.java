package com.nv95.fbchatnew.components.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.core.AccountStore;
import com.nv95.fbchatnew.utils.AvatarUtils;

/**
 * Created by nv95 on 14.08.16.
 */

public class AccountPreference extends Preference {

    public AccountPreference(Context context, AttributeSet attrs) {
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
        if (AccountStore.isAuthorized(view.getContext())) {
            AvatarUtils.assignAvatarTo(iv, AccountStore.getLogin(view.getContext()));
        } else {
            iv.setImageResource(R.drawable.ic_avatar_holder);
        }
    }
}
