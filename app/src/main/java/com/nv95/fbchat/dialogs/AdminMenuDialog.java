package com.nv95.fbchat.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.nv95.fbchat.ChatService;
import com.nv95.fbchat.R;
import com.nv95.fbchat.utils.ThemeUtils;

import java.util.ArrayList;

/**
 * Created by nv95 on 14.08.16.
 */

public class AdminMenuDialog implements DialogInterface.OnClickListener {

    private final Dialog mDialog;
    private final ChatService.ChatBinder mBinder;

    public AdminMenuDialog(Activity activity, ChatService.ChatBinder chatBinder) {
        mBinder = chatBinder;
        ArrayList<String> items = new ArrayList<>();
        if (chatBinder.isModer()) {
            items.add(activity.getString(R.string.create_room));
        }
        if (chatBinder.isAdmin()) {
            items.add(activity.getString(R.string.remove_room));
        }
        if (items.size() == 0) {
            mDialog = null;
            return;
        }
        mDialog = new AlertDialog.Builder(activity)
                .setItems(items.toArray(new String[items.size()]), this)
                .setTitle(chatBinder.isAdmin() ? R.string.admin : R.string.moder)
                .create();
        mDialog.setOwnerActivity(activity);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case 0: //add room
                new EditTextDialog(mDialog.getContext(), R.string.create_room, new EditTextDialog.OnTextChangedListener() {
                    @Override
                    public void onTextChanged(String newText) {
                        if (mBinder.createNewRoom(newText)) {
                            Toast.makeText(mDialog.getContext(), R.string.query_sent, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show(R.string.input_room_name, null);
                break;
            case 1: //remove room
                AlertDialog d = new AlertDialog.Builder(mDialog.getContext())
                        .setNegativeButton(android.R.string.cancel, null)
                        .setMessage(mDialog.getContext().getString(R.string.remove_room_confirm, mBinder.getCurrentRoomName()))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mBinder.removeRoom(mBinder.getCurrentRoomName())) {
                                    Toast.makeText(mDialog.getContext(), R.string.query_sent, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create();
                d.setOnShowListener(new ThemeUtils.DialogPainter());
                d.show();
                break;
        }
        dialogInterface.dismiss();
    }
}
