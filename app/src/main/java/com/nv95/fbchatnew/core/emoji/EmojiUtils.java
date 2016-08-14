package com.nv95.fbchatnew.core.emoji;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.nv95.fbchatnew.R;

import java.io.InputStream;

/**
 * Created by nv95 on 13.08.16.
 */

public class EmojiUtils {

    private static int mSizeSmall = 0;

    public static Bitmap getEmojiBitmap(Context context, int index, int size) {
        AssetManager am = context.getAssets();
        InputStream in = null;
        try {
            in = am.open(index + ".png");
        } catch (Exception e){
            e.printStackTrace();
        }
        Bitmap src = BitmapFactory.decodeStream(in, null, null);
        Bitmap res = Bitmap.createScaledBitmap(src, size, size, false);
        src.recycle();
        return res;
    }

    public static Bitmap getEmojiBitmap(Context context, int index) {
        AssetManager am = context.getAssets();
        InputStream in = null;
        try {
            in = am.open(index + ".png");
        } catch (Exception e){
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(in, null, null);
    }

    public static ImageSpan getEmojiSpan(Context context, int index) {
        if (mSizeSmall == 0) {
            mSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.emoji_size_small);
        }
        return new ImageSpan(context, getEmojiBitmap(context, index, mSizeSmall), DynamicDrawableSpan.ALIGN_BOTTOM);
    }

    public static SpannableString getEmojiString(Context context, int index) {
        SpannableString ss = new SpannableString(EmojiCompat.getEmojiString(index));
        ss.setSpan(getEmojiSpan(context, index), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString emojify(Context context, String source) {
        SpannableString ss = new SpannableString(source);
        char c;
        for (int i=0;i<ss.length()-1;i++) {
            c = ss.charAt(i);
            if (EmojiCompat.isEmoji(c)) {
                int ind = EmojiCompat.indexOf(c, ss.charAt(i+1));
                if (ind != -1) {
                    ss.setSpan(getEmojiSpan(context, ind), i, i + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    i++;
                }
            }
        }
        return ss;
    }

    public static int getEmojiCount() {
        return EmojiCompat.getMapSize();
    }
}
