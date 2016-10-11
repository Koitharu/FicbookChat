package com.nv95.fbchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.Html;

import com.nv95.fbchat.core.AccountStore;
import com.nv95.fbchat.core.ChatCallback;
import com.nv95.fbchat.core.ChatMessage;
import com.nv95.fbchat.core.FbChat;
import com.nv95.fbchat.core.Rooms;
import com.nv95.fbchat.utils.NotificationHelper;
import com.nv95.fbchat.utils.SpanUtils;
import com.nv95.fbchat.utils.TimestampUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 11.08.16.
 */

public class ChatService extends Service implements FbChat.ChatCallback {

    private static final int NOTIFY_ID = 333;
    public static final int POWER_ADMIN = 1000;
    public static final int POWER_MODER = 100;

    public static final String MAIN_URL = "ws://146.120.111.42:7070";
    public static final String DEBUG_URL = "ws://146.120.111.42:9000";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private FbChat mChat;
    @Nullable
    private ChatCallback mCallback;
    @Nullable
    private String mCurrentRoom;
    private int mCurrentOnline;
    private String mMyLogin;
    private int mPower;
    private int mNewMessages;
    private NotificationHelper mNotifyHelper;
    private boolean mBackgroundMode;

    @Override
    public void onCreate() {
        super.onCreate();
        mPower = 0;
        mNewMessages = 0;
        mCurrentOnline = 0;
        mBackgroundMode = false;
        mMyLogin = AccountStore.getLogin(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(R.drawable.stat_connecting);
        mNotificationBuilder.setContentTitle(getString(R.string.app_name));
        mNotificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        mNotificationBuilder.addAction(R.drawable.ic_sym_quit, getString(R.string.exit),
                PendingIntent.getService(this, 1, new Intent(this, ChatService.class).putExtra("action", "exit"), 0));
        mNotificationBuilder.setContentText(getString(R.string.connecting));
        mCurrentRoom = null;
        mNotifyHelper = new NotificationHelper(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(mNotifyHelper);
        boolean debug = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("debug", false);
        mChat = new FbChat(debug ? DEBUG_URL : MAIN_URL, this);
        mChat.connect();
        startForeground(NOTIFY_ID, mNotificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        mCallback = null;
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(mNotifyHelper);
        mNotificationBuilder = null;
        mChat.disconnect();
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ChatBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mCallback = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("action")) {
            switch (intent.getStringExtra("action")) {
                case "exit":
                    if (mCallback != null) {
                        mCallback.onQuitByUser();
                    }
                    stopSelf();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onMessageReceived(JSONObject message) {
        try {
            switch (message.getString("type")) {
                case "status": {
                    switch (message.getString("action")) {
                        case "authorization":
                            if ("success".equals(message.getString("status"))) {
                                mNotificationBuilder.setSmallIcon(R.drawable.ic_stat_wechat);
                                mNotificationBuilder.setContentText(getString(R.string.online));
                                mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
                                mCallback.onAuthorizationSuccessful(
                                        mMyLogin = message.getString("login"),
                                        message.getString("password"),
                                        mPower = message.getInt("power")
                                );
                            } else if ("error".equals(message.getString("status"))) {
                                mCallback.onAuthorizationFailed(
                                        Html.fromHtml(message.getString("message")).toString().trim()
                                );
                            }
                            break;
                    }
                    break;
                }
                case "rooms": {
                    if (mCallback != null) {
                        Rooms rooms = new Rooms();
                        JSONArray ja = message.getJSONArray("list");
                        for (int i = 0; i < ja.length(); i++) {
                            rooms.add(new Rooms.Item(ja.getJSONObject(i)));
                        }
                        mCallback.onRoomsUpdated(rooms, mCurrentRoom);
                    }
                    break;
                }
                case "event": {
                    String msg;
                    switch (message.getString("action")) {
                        case "custom":
                            if (mCallback != null) {
                                mCallback.onMessageReceived(
                                        new ChatMessage(
                                                null,
                                                message.getString("message")
                                        )
                                );
                            }
                            return;
                        case "join":
                            msg = getString(R.string.joined);
                            break;
                        case "leave":
                            msg = getString(R.string.left);
                            break;
                        case "kiked":
                            msg = getString(R.string.kiked);
                            break;
                        default:
                            return;
                    }
                    ChatMessage cm = new ChatMessage(
                            message.getString("user_name"),
                            msg
                    );
                    if (mCallback != null) {
                        mCallback.onMessageReceived(cm);
                        mCallback.onUserCountChanged(mCurrentOnline = message.getInt("users_count"));
                    }
                    break;
                }
                case "room": {
                    if (mCallback != null && "about".equals(message.getString("object"))) {
                        mCallback.onRoomInfo(message.getString("about"));
                    }
                    break;
                }
                case "history": {
                    ArrayList<ChatMessage> lst = new ArrayList<>();
                    JSONArray ja = message.getJSONArray("messages");
                    ChatMessage cm;
                    for (int i = 0; i < ja.length(); i++) {
                        cm = new ChatMessage(this, ja.getJSONObject(i), mMyLogin);
                        cm.type = mMyLogin.equals(cm.login) ? ChatMessage.MSG_MY : ChatMessage.MSG_NORMAL;
                        lst.add(cm);
                    }
                    if (mCallback != null) {
                        mCallback.onHistoryReceived(lst, message.getString("name"));
                    }
                    break;
                }
                case "chat":
                    switch (message.getString("object")) {
                        case "message": {
                            if (message.getString("room_name").equals(mCurrentRoom)) {
                                ChatMessage cm = new ChatMessage(
                                        message.getString("user"),
                                        SpanUtils.makeSpans(this, message.getString("message"), mMyLogin),
                                        message.getLong("time")
                                );
                                cm.type = mMyLogin.equals(cm.login) ? ChatMessage.MSG_MY : ChatMessage.MSG_NORMAL;
                                if (mCallback != null) {
                                    mCallback.onMessageReceived(cm);
                                }
                                if (mBackgroundMode) {
                                    mNotifyHelper.notify(cm, ChatMessage.hasMention(cm.message, mMyLogin));
                                    mNewMessages++;
                                    if (mNotificationBuilder != null) {
                                        mNotificationBuilder.setContentText(
                                                getResources().getQuantityString(R.plurals.new_messages, mNewMessages, mNewMessages)
                                        );
                                        mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
                                    }
                                }
                            }
                            break;
                        }
                        case "participants": {
                            JSONArray ja = message.getJSONArray("participants");
                            List<String> pcp = new ArrayList<>(ja.length());
                            for (int i = 0; i < ja.length(); i++) {
                                pcp.add(ja.getString(i));
                            }
                            if (mCallback != null) {
                                mCallback.onOnlineListReceived(message.getString("room_name"), pcp);
                            }
                            break;
                        }
                        case "search": {
                            ArrayList<ChatMessage> lst = new ArrayList<>();
                            JSONArray ja = message.getJSONArray("history");
                            ChatMessage cm;
                            for (int i = 0; i < ja.length(); i++) {
                                cm = new ChatMessage(this, ja.getJSONObject(i), mMyLogin);
                                cm.type = mMyLogin.equals(cm.login) ? ChatMessage.MSG_MY : ChatMessage.MSG_NORMAL;
                                lst.add(cm);
                            }
                            if (mCallback != null) {
                                mCallback.onSearchResult(lst, message.getString("query"));
                            }
                            break;
                        }
                    }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onAlertMessage("Ошибка на клиенте:\n\n" + e.getMessage(), true, false);
            }
        }
    }

    @Override
    public void onConnected() {
        if (mCallback != null) {
            mCallback.onConnected();
        }
    }

    @Override
    public void onDisconnected(String reason) {
        if (mNotificationBuilder != null) {
            mNotificationBuilder.setSmallIcon(R.drawable.ic_stat_disconnect);
            mNotificationBuilder.setContentText(getString(R.string.disconnected));
            mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
        }
        if (mCallback != null) {
            mCallback.onAlertMessage(getString(R.string.disconnected) + "\n\n" + reason, true, true);
        }
    }

    public class ChatBinder extends Binder {

        public void setCallback(ChatCallback callback) {
            mCallback = callback;
            if (mChat.isConnected() && mCallback != null) {
                mCallback.onConnected();
            }
        }

        public boolean signIn(String login, String password) {
            mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "autorize");
                jo.put("login", login);
                jo.put("password", password);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean requestRooms() {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "rooms");
                jo.put("action", "get");
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean sendMessage(String msg) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "chat");
                jo.put("action", "send");
                jo.put("room_name", getCurrentRoomName());
                jo.put("subject", "message");
                jo.put("message", msg);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean requestMessagesHistory(long timestamp, int count) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "chat");
                jo.put("action", "get");
                jo.put("subject", "history");
                jo.put("room_name", mCurrentRoom);
                jo.put("timestamp", timestamp);
                jo.put("count", count);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean joinRoom(String name) {
            try {
                mCurrentRoom = name;
                getSharedPreferences("chat", MODE_PRIVATE)
                        .edit()
                        .putString("room_name", mCurrentRoom)
                        .apply();
                JSONObject jo = new JSONObject();
                jo.put("type", "room");
                jo.put("action", "join");
                jo.put("room_name", name);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean isAdmin() {
            return mPower >= POWER_ADMIN;
        }

        public boolean isModer() {
            return mPower >= POWER_MODER;
        }

        public boolean requestRoomMembers(@Nullable String roomName) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "chat");
                jo.put("subject", "participants");
                jo.put("action", "get");
                jo.put("room_name", roomName == null ? mCurrentRoom : roomName);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }


        public boolean createNewRoom(String name) {
            if (!isModer()) {
                return false;
            }
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "administration");
                jo.put("object", "room");
                jo.put("action", "create");
                jo.put("name", name);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean removeRoom(String name) {
            if (!isAdmin()) {
                return false;
            }
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "administration");
                jo.put("object", "room");
                jo.put("action", "destroy");
                jo.put("room_name", name);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean banhammer(String userName, int hours, String reason) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "administration");
                jo.put("message", reason);
                jo.put("action", hours == 0 ? "kik" : "ban");
                jo.put("user_name", userName);
                if (hours != 0) {
                    jo.put("duration", (long)(hours * TimestampUtils.HOUR));
                }
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean search(String query) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("type", "chat");
                jo.put("action", "search");
                jo.put("room_name", mCurrentRoom);
                jo.put("query", query);
                mChat.send(jo);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Nullable
        public String getCurrentRoomName() {
            return mCurrentRoom;
        }

        public void terminate() {
            stopSelf();
        }

        public String getMe() {
            return mMyLogin;
        }

        public int getCurrentOnline() {
            return mCurrentOnline;
        }

        public void setBackground(boolean isBackground) {
            mBackgroundMode = isBackground;
            mNewMessages = 0;
            if (mNotificationBuilder != null && mChat.isConnected()) {
                mNotificationBuilder.setContentText(getString(R.string.online));
                mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
            }
        }
    }
}
