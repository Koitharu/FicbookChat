package com.nv95.fbchat.components;

import android.support.design.widget.AppBarLayout;
import android.widget.ImageView;

/**
 * Created by nv95 on 16.08.16.
 */

public class AvatarBehavior implements AppBarLayout.OnOffsetChangedListener {

    private final ImageView mImageView;
    private final float mBaseTranslationY;

    private AvatarBehavior(AppBarLayout appBarLayout, ImageView imageView) {
        mImageView = imageView;
        appBarLayout.addOnOffsetChangedListener(this);
        mBaseTranslationY = mImageView.getTranslationY();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int h = appBarLayout.getLayoutParams().height;
        float t = (h + verticalOffset) / (float)h;
        t = Math.max(t, 0.52f);
        mImageView.setScaleY(t);
        mImageView.setScaleX(t);
        mImageView.setTranslationY(mBaseTranslationY - verticalOffset / 1.9f);
    }

    public static void link(AppBarLayout appBarLayout, ImageView imageView) {
        new AvatarBehavior(appBarLayout, imageView);
    }
}
