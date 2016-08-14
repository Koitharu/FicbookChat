package com.nv95.fbchatnew.core.emoji;

/**
 * Created by nv95 on 13.08.16.
 */

public class EmojiCompat {

    private static final char EMOJI_MAIN = '\uD83D';
    private static final char EMOJI_CUSTOM = '\uD83C';

    private static final char[][] mEmojiMap = new char[][] {
            new char[]{
                    '\uDE01',
                    '\uDE02',
                    '\uDE03',
                    '\uDE04',
                    '\uDE05',
                    '\uDE06',
                    '\uDE09',
                    '\uDE0A',
                    '\uDE0B',
                    '\uDE0C',
                    '\uDE0D',
                    '\uDE0F',
                    '\uDE12',
                    '\uDE13',
                    '\uDE14',
                    '\uDE16',
                    '\uDE18',
                    '\uDE1A',
                    '\uDE1C',
                    '\uDE1D',
                    '\uDE1E',
                    '\uDE20',
                    '\uDE21',
                    '\uDE22',
                    '\uDE23',
                    '\uDE24',
                    '\uDE25',
                    '\uDE28',
                    '\uDE29',
                    '\uDE2A',
                    '\uDE2B',
                    '\uDE2D',
                    '\uDE30',
                    '\uDE31',
                    '\uDE32',
                    '\uDE33',
                    '\uDE35',
                    '\uDE37',
                    '\uDE38',
                    '\uDE39',
                    '\uDE3A',
                    '\uDE3B',
                    '\uDE3C',
                    '\uDE3D',
                    '\uDE3E',
                    '\uDE3F',
                    '\uDE40',
                    '\uDE48',
                    '\uDE49',
                    '\uDE4A',
                    '\uDE45',
                    '\uDE46',
                    '\uDE47'
            }, new char[] {
                    '\uDF7A',
                    '\uDF7B',
                    '\uDC25',
                    '\uDC2D',
                    '\uDC37',
                    '\uDC38',
                    '\uDC40',
                    '\uDC4B',
                    '\uDC4C',
                    '\uDCA4',
            }
    };

    public static boolean isEmoji(char c) {
        return c == EMOJI_MAIN || c == EMOJI_CUSTOM;
    }


    public static int indexOf(char c1, char c2) {
        switch (c1) {
            case EMOJI_MAIN:
                return indexOf(mEmojiMap[0], c2);
            case EMOJI_CUSTOM:
                return indexOf(mEmojiMap[1], c2) + mEmojiMap[0].length;
            default:
                return -1;
        }
    }

    private static int indexOf(char[] a, char c) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public static String getEmojiString(int index) {
        if (index < mEmojiMap[0].length) {
            return EMOJI_MAIN + "" + mEmojiMap[0][index];
        } else return EMOJI_CUSTOM + "" + mEmojiMap[1][index - mEmojiMap[0].length];
    }

    public static int getMapSize() {
        return mEmojiMap[0].length + mEmojiMap[1].length;
    }
}
