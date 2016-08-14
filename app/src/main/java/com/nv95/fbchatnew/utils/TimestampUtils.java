package com.nv95.fbchatnew.utils;

import android.text.format.DateUtils;

/**
 * Created by nv95 on 12.08.16.
 */

public class TimestampUtils {

    public static final long HOUR = 3600000L;

    public static long getDiffMinutes(long ts1, long ts2) {
        return Math.abs(ts1 - ts2) / 60000;
    }

    public static String formatRelative(long ts) {
        return DateUtils.getRelativeTimeSpanString(ts, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL).toString();
    }
}
