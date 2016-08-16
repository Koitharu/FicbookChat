package com.nv95.fbchatnew.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.components.CenteredImageSpan;
import com.nv95.fbchatnew.components.ChipsSpan;
import com.nv95.fbchatnew.core.emoji.EmojiCompat;

/**
 * Created by nv95 on 13.08.16.
 */

public class SpanUtils {

    private static int mSizeSmall = 0;

    public static ImageSpan getEmojiSpan(Context context, int index) {
        if (mSizeSmall == 0) {
            mSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.emoji_size_small);
        }
        return new CenteredImageSpan(context, EmojiCompat.getEmojiBitmap(context, index, mSizeSmall));
    }

    public static SpannableString getEmojiString(Context context, int index) {
        SpannableString ss = new SpannableString(EmojiCompat.getEmojiString(index));
        ss.setSpan(getEmojiSpan(context, index), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    @Deprecated
    public static Spanned emojify(Context context, String source) {
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

    private static ChipsSpan getUserSpan(Context context, String nickname) {
        DayNightPalette palette = DayNightPalette.fromString(nickname, ChatApp.getApplicationPalette().isDark());
        return new ChipsSpan(
                context,
                ContextCompat.getColor(context, R.color.gray_60),
                palette.getContrastColor()

        );
    }

    public static Spanned getUserString(Context context, String nickname) {
        SpannableString ss = new SpannableString("@" + nickname.replace(" ", "\u00A0"));
        ss.setSpan(getUserSpan(context, nickname), 0, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static Spanned makeSpans(Context context, String source) {
        SpannableString ss = new SpannableString(source);
        char c;
        for (int i=0;i<ss.length()-1;i++) {
            c = ss.charAt(i);
            if (c == '@' && (i == 0 || ss.charAt(i-1) == ' ' || ss.charAt(i-1) == '\n')) {
                int to = i;
                while (to < ss.length() && ss.charAt(to) != ' ' && ss.charAt(to) != '\n') {
                    to++;
                }
                ss.setSpan(getUserSpan(context, ss.subSequence(i+1,to).toString().replace("\u00A0", " ")),
                        i, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } if (EmojiCompat.isEmoji(c)) {
                int ind = EmojiCompat.indexOf(c, ss.charAt(i+1));
                if (ind != -1) {
                    ss.setSpan(getEmojiSpan(context, ind), i, i + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    i++;
                }
            }
        }
        return ss;
    }

    private static final Rect textBounds = new Rect();

    public static Drawable getCounterIcon(Context context, String text) {
        int size = LayoutUtils.DpToPx(context.getResources(), 24);
        int corners = LayoutUtils.DpToPx(context.getResources(), 4);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0, 0, size, size), corners, corners, paint);

        paint.setFakeBoldText(true);
        paint.setColor(ChatApp.getApplicationPalette().getDarkColor());
        paint.setTextSize(LayoutUtils.DpToPx(context.getResources(), 12));

        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, (size / 2) - textBounds.exactCenterX(), (size / 2) - textBounds.exactCenterY(), paint);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
