package com.nv95.fbchat.core;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.nv95.fbchat.utils.SpanUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nv95 on 11.08.16.
 */

public class ChatMessage implements Parcelable {

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

    protected ChatMessage(Parcel in) {
        type = in.readInt();
        login = in.readString();
        timestamp = in.readLong();
        mMentioned = in.readByte() != 0;
        message = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public static boolean hasMention(CharSequence message, String myLogin) {
        return myLogin != null && message.toString().contains("@" + myLogin.replace(" ", "\u00A0"));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(type);
        parcel.writeString(login);
        parcel.writeLong(timestamp);
        parcel.writeByte((byte) (mMentioned ? 1 : 0));
        TextUtils.writeToParcel(message, parcel, 0);
    }
}
