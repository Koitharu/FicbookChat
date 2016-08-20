package com.nv95.fbchat.core;

import android.content.Context;

import com.nv95.fbchat.utils.SpanUtils;

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
    private boolean mMentioned = false;

    public ChatMessage(String login, CharSequence message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.login = login;
    }

    public ChatMessage(Context context, JSONObject jo, String myLogin) throws JSONException {
        message = SpanUtils.makeSpans(context, jo.getString("message"), myLogin);
        timestamp = jo.getLong("timestamp");
        login = jo.getString("login");
    }

    public ChatMessage(String user, String event) {
        type = MSG_EVENT;
        message = event;
        login = user;
        timestamp = System.currentTimeMillis();
    }

    public static boolean hasMention(CharSequence message, String myLogin) {
        return myLogin != null && message.toString().contains("@" + myLogin.replace(" ", "\u00A0"));
    }
}
