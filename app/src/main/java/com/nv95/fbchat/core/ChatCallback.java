package com.nv95.fbchat.core;

import com.nv95.fbchat.dialogs.BanLogDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 11.08.16.
 */

public interface ChatCallback {
    void onConnected();

    void onAuthorizationFailed(String reason);

    void onAuthorizationSuccessful(String login, String password, int power);

    void onRoomsUpdated(Rooms rooms, String current);

    void onMessageReceived(ChatMessage message);

    void onHistoryReceived(List<ChatMessage> history, String room);

    void onAlertMessage(String message, boolean error, boolean quit);

    void onOnlineListReceived(String room, List<String> participants);

    void onUserCountChanged(int count);

    void onQuitByUser();

    void onSearchResult(ArrayList<ChatMessage> messages, String query);

    void onRoomInfo(String about);

    void onActiveBans(BanLogDialog.BanLogItem[] bans);
}
