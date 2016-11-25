package com.nv95.fbchat.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.nv95.fbchat.ChatService;
import com.nv95.fbchat.R;
import com.nv95.fbchat.utils.ThemeUtils;

/**
 * Created by nv95 on 14.08.16.
 */

public class BanhammerDialog implements DialogInterface.OnClickListener {

    private String mUserName;
    private final Dialog mDialog;
    private final ChatService.ChatBinder mBinder;

    public BanhammerDialog(Activity activity, ChatService.ChatBinder chatBinder) {
        mBinder = chatBinder;
        mDialog = new AlertDialog.Builder(activity)
                .setItems(R.array.banhammer_times, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mDialog.setOnShowListener(new ThemeUtils.DialogPainter());
        mDialog.setOwnerActivity(activity);
    }

    public void show(String username) {
        mUserName = username;
        mDialog.setTitle(mDialog.getContext().getString(R.string.banhammer) + " " + username);
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        final int hours = mDialog.getContext().getResources().getIntArray(R.array.banhammer_hours)[i];
        new EditTextDialog(mDialog.getContext(), R.string.banhammer, new EditTextDialog.OnTextChangedListener() {
            @Override
            public void onTextChanged(String newText) {
                mDialog.dismiss();
                mBinder.banhammer(mUserName, hours, newText);
                Toast.makeText(mDialog.getContext(), R.string.query_sent, Toast.LENGTH_SHORT).show();
            }
        }).show(R.string.reason, null);
    }

    public static void kikDialog(final Context context, final String username, final ChatService.ChatBinder binder) {
        /*new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.kik_confirm, username))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        binder.banhammer(username, 0, context.getString(R.string.you_kiked));
                        Toast.makeText(context, R.string.query_sent, Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();*/
        new EditTextDialog(context, R.string.kik, new EditTextDialog.OnTextChangedListener() {
            @Override
            public void onTextChanged(String newText) {
                binder.banhammer(username, 0, newText);
                Toast.makeText(context, R.string.query_sent, Toast.LENGTH_SHORT).show();
            }
        }).show(R.string.reason, null);
    }
}
