package com.nv95.fbchat.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by nv95 on 29.11.15.
 */
public class BubbleDrawable extends Drawable {

    public static int pointerSize = 0;

    private Paint paint;
    private boolean rtl;

    private int mBoxWidth;
    private int mBoxHeight;
    private float mCornerRad;
    private Rect mBoxPadding = new Rect();

    private Path pointer;
    private int mPointerWidth;
    private int mPointerHeight;

    public BubbleDrawable() {
        initBubble();
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setCornerRadius(float cornerRad) {
        mCornerRad = cornerRad;
    }

    public void setRtl(boolean rtl) {
        this.rtl = rtl;
    }

    public void setPointerWidth(int pointerWidth) {
        mPointerWidth = pointerWidth;
    }

    public void setPointerHeight(int pointerHeight) {
        mPointerHeight = pointerHeight;
    }

    private void initBubble() {
        paint = new Paint();
        paint.setAntiAlias(true);
        mCornerRad = 0;
        setPointerWidth(pointerSize);
        setPointerHeight(pointerSize);
    }

    private void updatePointerPath() {
        pointer = new Path();
        pointer.setFillType(Path.FillType.EVEN_ODD);
        pointer.moveTo(
                rtl ? mBoxWidth : 0,
                rtl ? mBoxHeight : 0
        );
        pointer.lineTo(
                rtl ? mBoxWidth - mPointerWidth : mPointerWidth,
                rtl ? mBoxHeight : 0
        );
        pointer.lineTo(
                rtl ? mBoxWidth - mPointerWidth : mPointerWidth,
                rtl ? mBoxHeight - mPointerHeight : mPointerHeight
        );
        pointer.close();
    }

    @Override
    public void draw(Canvas canvas) {
        RectF mBoxRect = new RectF(rtl ? 0 : mPointerWidth, 0.0f, mBoxWidth - (rtl ? mPointerWidth : 0), mBoxHeight);
        canvas.drawRoundRect(mBoxRect, mCornerRad, mCornerRad, paint);
        updatePointerPath();
        canvas.drawPath(pointer, paint);
    }

    @Override
    public int getOpacity() {
        return 255;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    /*@Override
    public boolean getPadding(@NonNull Rect padding) {
        padding.set(mBoxPadding);
        if (rtl) {
            padding.right += mPointerWidth;
        } else {
            padding.left += mPointerWidth;
        }
        return true;
    }**/

    @Override
    protected void onBoundsChange(Rect bounds) {
        mBoxWidth = bounds.width();
        mBoxHeight = getBounds().height();
        super.onBoundsChange(bounds);
    }
}