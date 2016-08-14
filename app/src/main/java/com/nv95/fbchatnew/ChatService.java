package com.nv95.fbchatnew;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.nv95.fbchatnew.core.AccountStore;
import com.nv95.fbchatnew.core.ChatCallback;
import com.nv95.fbchatnew.core.ChatMessage;
import com.nv95.fbchatnew.core.FbChat;
import com.nv95.fbchatnew.core.Rooms;
import com.nv95.fbchatnew.core.emoji.EmojiUtils;

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

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private FbChat mChat;
    @Nullable
    private ChatCallback mCallback;
    @Nullable
    private String mCurrentRoom;
    private String mMyLogin;

    @Override
    public void onCreate() {
        super.onCreate();
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
        mChat = new FbChat(this);
        mChat.connect();
        startForeground(NOTIFY_ID, mNotificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        mCallback = null;
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
                                        message.getString("password")
                                );
                            } else if ("error".equals(message.getString("status"))) {
                                mCallback.onAuthorizationFailed(
                                        message.getString("message")
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
                    mCallback.onMessageReceived(cm);
                    break;
                }
                case "history": {
                    ArrayList<ChatMessage> lst = new ArrayList<>();
                    JSONArray ja = message.getJSONArray("messages");
                    ChatMessage cm;
                    for (int i = 0; i < ja.length(); i++) {
                        cm = new ChatMessage(this, ja.getJSONObject(i));
                        cm.type = mMyLogin.equals(cm.login) ? ChatMessage.MSG_MY : ChatMessage.MSG_NORMAL;
                        lst.add(cm);
                    }
                    mCallback.onHistoryReceived(lst, message.getString("name"));
                    break;
                }
                case "chat":
                    switch (message.getString("object")) {
                        case "message": {
                            if (message.getString("room_name").equals(mCurrentRoom)) {
                                ChatMessage cm = new ChatMessage(
                                        message.getString("user"),
                                        EmojiUtils.emojify(this, message.getString("message")),
                                        message.getLong("time")
                                );
                                cm.type = mMyLogin.equals(cm.login) ? ChatMessage.MSG_MY : ChatMessage.MSG_NORMAL;
                                mCallback.onMessageReceived(cm);
                            }
                            break;
                        }
                        case "participants": {
                            JSONArray ja = message.getJSONArray("participants");
                            List<String> pcp = new ArrayList<>(ja.length());
                            for (int i=0;i<ja.length();i++) {
                                pcp.add(ja.getString(i));
                            }
                            mCallback.onOnlineListReceived(message.getString("room_name"), pcp);
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

        @Nullable
        public String getCurrentRoomName() {
            return mCurrentRoom;
        }

        public void terminate() {
            stopSelf();
        }
    }
}
