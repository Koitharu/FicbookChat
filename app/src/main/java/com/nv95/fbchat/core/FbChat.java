package com.nv95.fbchat.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.core.websocket.WebSocketClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by nv95 on 11.08.16.
 */

public class FbChat implements WebSocketClient.Listener, Handler.Callback {

    private static final int MSG_CONNECT = 1;
    private static final int MSG_DISCONNECT = 2;
    private static final int MSG_MESSAGE = 3;

    private final WebSocketClient mWebSocket;
    private final ChatCallback mCallback;
    private boolean mConnected;
    private final Handler mHandler;

    public FbChat(ChatCallback callback) {
        mHandler = new Handler(this);
        mWebSocket = new WebSocketClient(URI.create(ChatApp.CHAT_URL), this, new ArrayList<BasicNameValuePair>());
        mCallback = callback;
        mConnected = false;
    }

    @Override
    public void onConnect() {
        mHandler.sendEmptyMessage(MSG_CONNECT);
    }

    @Override
    public void onMessage(String message) {
        Message msg = new Message();
        msg.obj = message;
        msg.what = MSG_MESSAGE;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onMessage(byte[] data) {

    }

    @Override
    public void onDisconnect(int code, String reason) {
        Message msg = new Message();
        msg.obj = reason == null ? "" : reason;
        msg.what = MSG_DISCONNECT;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onError(Exception error) {
        Message msg = new Message();
        msg.obj = error.getMessage();
        msg.what = MSG_DISCONNECT;
        mHandler.sendMessage(msg);
    }

    public void connect() {
        mWebSocket.connect();
    }

    public void disconnect() {
        mWebSocket.disconnect();
    }

    public void send(JSONObject data) {
        mWebSocket.send(data.toString());
    }

    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_CONNECT:
                mConnected = true;
                mCallback.onConnected();
                return true;
            case MSG_DISCONNECT:
                mCallback.onDisconnected((String) message.obj);
                return true;
            case MSG_MESSAGE:
                Log.d("WS", message.obj.toString());
                try {
                    mCallback.onMessageReceived(new JSONObject((String) message.obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return false;
        }
    }

    public interface ChatCallback {
        void onMessageReceived(JSONObject message);
        void onConnected();
        void onDisconnected(String reason);
    }
}
