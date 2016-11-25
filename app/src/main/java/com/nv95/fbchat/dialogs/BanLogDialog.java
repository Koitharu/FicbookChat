package com.nv95.fbchat.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchat.R;
import com.nv95.fbchat.utils.AvatarUtils;
import com.nv95.fbchat.utils.ThemeUtils;
import com.nv95.fbchat.utils.TimestampUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nv95 on 25.11.16.
 */

public class BanLogDialog implements DialogInterface.OnClickListener {

    private final AlertDialog mDialog;

    public BanLogDialog(Context context, BanLogItem[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.banned_list)
                .setNegativeButton(R.string.close, null);
        if (items.length == 0) {
            builder.setMessage(R.string.no_active_bans);
        } else {
            builder.setAdapter(new LogAdapter(context, items), this);
        }
        mDialog = builder.create();
        mDialog.setOnShowListener(new ThemeUtils.DialogPainter());
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    private class LogAdapter extends ArrayAdapter<BanLogItem> {

        LogAdapter(Context context, BanLogItem[] objects) {
            super(context, R.layout.item_banlog, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banlog, parent, false);
            }
            BanLogItem item = getItem(position);
            if (item != null) {
                AvatarUtils.assignAvatarTo((ImageView) convertView.findViewById(R.id.imageViewAvatar), item.subject);
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.subject);
                ((TextView) convertView.findViewById(android.R.id.text2)).setText(TimestampUtils.formatRelative(item.expires));
                ((TextView) convertView.findViewById(R.id.textView)).setText(item.reason + " [" + item.admin + "]");
            } else {
                ((ImageView) convertView.findViewById(R.id.imageViewAvatar)).setImageResource(R.drawable.ic_avatar_holder);
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(R.string.error);
                ((TextView) convertView.findViewById(android.R.id.text2)).setText("");
                ((TextView) convertView.findViewById(R.id.textView)).setText("");
            }
            return convertView;
        }
    }


    public static class BanLogItem {

        final String subject;
        final String admin;
        final String reason;
        final long expires;

        public BanLogItem(JSONObject jo) throws JSONException {
            subject = jo.getString("login_banned");
            admin = jo.getString("login_banning");
            reason = jo.getString("reason");
            expires = jo.getLong("time_expired");
        }
    }
}
