package com.nv95.fbchat.components.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.R;
import com.nv95.fbchat.utils.MediaUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by nv95 on 20.08.16.
 */

public class ImagePreference extends Preference implements PreferenceManager.OnActivityResultListener {

    public static final int REQUEST_PICK = 1212;

    private String mValue;
    private boolean mValueSet;
    private ImageView mImageView;
    private ImageView mImageViewClear;

    private static final DisplayImageOptions mOptions = ChatApp.getImageLoaderOptionsBuilder()
            .resetViewBeforeLoading(true)
            .build();

    public ImagePreference(Context context) {
        super(context);
    }

    public ImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pref_img, parent, false);
        mImageView = (ImageView) layout.findViewById(R.id.imageView);
        mImageViewClear = (ImageView) layout.findViewById(R.id.imageViewClear);
        mImageViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("");
                MediaUtils.cleanDir(view.getContext().getExternalFilesDir("wallpaper"));
            }
        });
        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (TextUtils.isEmpty(mValue)) {
            mImageViewClear.setVisibility(View.GONE);
        } else {
            mImageViewClear.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("file://" + mValue, mImageView, mOptions);
        }
    }

    private void setValue(String value) {
        final boolean changed = mValue != null && !mValue.equals(value);
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistString(value);
            if (changed) {
                OnPreferenceChangeListener cl = getOnPreferenceChangeListener();
                if (cl != null) {
                    cl.onPreferenceChange(this, value);
                }
                notifyChanged();
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setValue(Crop.getOutput(data).getPath());
        }
        return true;
    }

    @Nullable
    public File getFile() {
        return TextUtils.isEmpty(mValue) ? null : new File(mValue);
    }
}
