package com.nv95.fbchat.components;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * User: Bazlur Rahman Rokon
 * Date: 9/7/13 - 3:33 AM
 */
public class ExpandableTextView extends TextView {

    private static final int DEFAULT_TRIM_LENGTH = 300;

    private CharSequence mOriginalText;
    private CharSequence mTrimmedText;
    private BufferType mBufferType;
    private boolean mTrimmed = true;

    public ExpandableTextView(Context context) {
        this(context, null);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrimmed) {
                    mTrimmed = false;
                    setText();
                    requestFocusFromTouch();
                }
            }
        });
    }

    @Override
    public boolean hasOnClickListeners() {
        return mTrimmed;
    }

    private void setText() {
        super.setText(getDisplayableText(), mBufferType);
    }

    private CharSequence getDisplayableText() {
        return mTrimmed ? mTrimmedText : mOriginalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOriginalText = text;
        mTrimmed = mOriginalText != null && mOriginalText.length() > DEFAULT_TRIM_LENGTH;
        mTrimmedText = getTrimmedText();
        mBufferType = type;
        setText();
    }

    private CharSequence getTrimmedText() {
        if (mTrimmed) {
            return new SpannableStringBuilder(mOriginalText, 0, DEFAULT_TRIM_LENGTH + 1).append(getElipsis());
        } else {
            return mOriginalText;
        }
    }

    private CharSequence getElipsis() {
        SpannableString ss = new SpannableString("...\nПоказать полностью");
        ss.setSpan(new ForegroundColorSpan(getLinkTextColors().getDefaultColor()), 4, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), 4, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public CharSequence getOriginalText() {
        return mOriginalText;
    }
}