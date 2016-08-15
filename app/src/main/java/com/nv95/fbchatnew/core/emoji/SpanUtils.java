package com.nv95.fbchatnew.core.emoji;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.utils.DayNightPalette;

/**
 * Created by nv95 on 13.08.16.
 */

public class SpanUtils {

    private static int mSizeSmall = 0;

    public static ImageSpan getEmojiSpan(Context context, int index) {
        if (mSizeSmall == 0) {
            mSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.emoji_size_small);
        }
        return new ImageSpan(context, EmojiCompat.getEmojiBitmap(context, index, mSizeSmall), DynamicDrawableSpan.ALIGN_BOTTOM);
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

    private static BackgroundColorSpan getUserSpan(String nickname) {
        return new BackgroundColorSpan(DayNightPalette.fromString(nickname, ChatApp.getApplicationPalette().isDark())
                .getCompatColor());
    }

    public static Spanned getUserString(String nickname) {
        SpannableString ss = new SpannableString("@" + nickname.replace(" ", "\u00A0"));
        ss.setSpan(getUserSpan(nickname), 0, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
                ss.setSpan(getUserSpan(ss.subSequence(i+1,to).toString().replace("\u00A0", " ")), i, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
}
