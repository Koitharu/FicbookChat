package com.nv95.fbchat.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nv95.fbchat.ChatApp;

/**
 * Created by nv95 on 20.08.16.
 */

public class WallpaperView extends View {

    @Nullable
    private Bitmap mWallpaper = null;
    private String mWallpaperFile = null;
    @Nullable
    private ImageSize mImageSize = null;
    private static final RectF mDestRect = new RectF();

    public WallpaperView(Context context) {
        super(context);
    }

    public WallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WallpaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WallpaperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mImageSize == null || (w > oldw || h > oldh)) {
            mImageSize = new ImageSize(w, h);
            loadWallpaper();
        }
    }

    public void setWallpaper(String filename) {
        mWallpaperFile = filename;
        loadWallpaper();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWallpaper != null && mImageSize != null) {
            int sourceWidth = mWallpaper.getWidth();
            int sourceHeight = mWallpaper.getHeight();

            float xScale = (float) mImageSize.getWidth() / sourceWidth;
            float yScale = (float) mImageSize.getHeight() / sourceHeight;
            float scale = Math.max(xScale, yScale);

            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            float left = (mImageSize.getWidth() - scaledWidth) / 2;
            float top = mImageSize.getHeight() - scaledHeight;

            mDestRect.set(left, top, left + scaledWidth, top + scaledHeight);

            canvas.drawBitmap(mWallpaper, null, mDestRect, null);
        }
    }

    private void loadWallpaper() {
        if (mImageSize == null || TextUtils.isEmpty(mWallpaperFile)) {
            mWallpaper = null;
            return;
        }
        mWallpaper = ImageLoader.getInstance().loadImageSync("file://" + mWallpaperFile,
                mImageSize,
                ChatApp.getImageLoaderOptionsBuilder()
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .build());
    }
}
