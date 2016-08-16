package com.nv95.fbchatnew.core;

import android.content.Context;

import com.nv95.fbchatnew.utils.SpanUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nv95 on 11.08.16.
 */

public class ChatMessage {

    public static final int MSG_NORMAL = 0;
    public static final int MSG_MY = 1;
    public static final int MSG_EVENT = 2;

    public int type;
    public final CharSequence message;
    public final String login;
    public final long timestamp;

    public ChatMessage(String login, CharSequence message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.login = login;
    }

    @Deprecated
    public ChatMessage(JSONObject jo) throws JSONException {
        message = jo.getString("message");
        timestamp = jo.getLong("timestamp");
        login = jo.getString("login");
    }

    public ChatMessage(Context context, JSONObject jo) throws JSONException {
        message = SpanUtils.makeSpans(context, jo.getString("message"));
        timestamp = jo.getLong("timestamp");
        login = jo.getString("login");
    }

    public ChatMessage(String user, String event) {
        type = MSG_EVENT;
        message = event;
        login = user;
        timestamp = System.currentTimeMillis();
    }
}
