package com.nv95.fbchatnew.components;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.utils.LayoutUtils;
import com.nv95.fbchatnew.utils.Palette;

/**
 * Created by nv95 on 13.08.16.
 */

public class ColorPreference extends Preference implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private int mValue;
    private boolean mValueSet;
    private final Dialog mDialog;
    private final View  mContentView;
    private final AppCompatSeekBar mSeekBar;
    private final Palette mPalette;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mValueSet = false;
        mPalette = Palette.fromColor(ChatApp.getApplicationPalette().getNormalColor());
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_color, null, false);
        mContentView = v.findViewById(R.id.content);
        mSeekBar = (AppCompatSeekBar) v.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mDialog = new AlertDialog.Builder(context)
                .setView(v)
                .setTitle(getTitle())
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected View onCreateView(ViewGroup parent) {
        View layout = LayoutInflater.from(
                getContext())
                .inflate(R.layout.pref_color, parent, false);
        ((TextView) layout.findViewById(android.R.id.title)).setText(getTitle());
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(ChatApp.getApplicationPalette().getDarkColor());
        shape.setStroke(LayoutUtils.DpToPx(parent.getResources(), 4), mPalette.getAccentColor());
        layout.findViewById(R.id.imageView).setBackgroundDrawable(shape);
        return layout;
    }

    @Override
    protected void onClick() {
        super.onClick();
        mSeekBar.setProgress(ChatApp.getApplicationPalette().getValue());
        mContentView.setBackgroundColor(mPalette.getDarkColor());
        Drawable d = mSeekBar.getProgressDrawable();
        if (d != null) {
            d.setColorFilter(mPalette.getAccentColor(), PorterDuff.Mode.MULTIPLY);
        }
        mDialog.show();
    }

    public void setValue(int value) {
        // Always persist/notify the first time.
        final boolean changed = mValue != value;
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistInt(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mValue) : (int) defaultValue);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        ChatApp.getApplicationPalette().setValue(mSeekBar.getProgress());
        setValue(mSeekBar.getProgress());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mPalette.setValue(i);
        mContentView.setBackgroundColor(mPalette.getDarkColor());
        Drawable d = mSeekBar.getProgressDrawable();
        if (d != null) {
            d.setColorFilter(mPalette.getAccentColor(), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
