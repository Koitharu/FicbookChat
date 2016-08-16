package com.nv95.fbchatnew.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

import com.nv95.fbchatnew.utils.LayoutUtils;

/**
 * Created by nv95 on 16.08.16.
 */

public class ChipsSpan extends ReplacementSpan {

    private final int mCornerSize;
    private final int mBgColor;
    private final int mFgColor;

    public ChipsSpan(Context context, int backgroundColor, int textColor) {
        mCornerSize = LayoutUtils.DpToPx(context.getResources(), 5);
        mBgColor = backgroundColor;
        mFgColor = textColor;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
        paint.setColor(mBgColor);
        canvas.drawRoundRect(rect, mCornerSize, mCornerSize, paint);
        paint.setColor(mFgColor);
        canvas.drawText(text, start, end, x + mCornerSize, y - 0.5f, paint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(measureText(paint, text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end) + mCornerSize * 2;
    }
}