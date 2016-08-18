package com.nv95.fbchat.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.R;
import com.nv95.fbchat.UserListAdapter;
import com.nv95.fbchat.utils.LayoutUtils;

import java.util.List;

/**
 * Created by nv95 on 12.08.16.
 */

public class UserListDialog implements View.OnClickListener, OnUserClickListener {

    private final Dialog mDialog;
    private final RecyclerView mRecyclerView;
    @Nullable
    private OnUserClickListener mClickListener = null;

    public UserListDialog(Activity activity) {
        mDialog = new Dialog(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_userlist, null, false);
        Button closeButton = (Button) view.findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(this);
        closeButton.setTextColor(ChatApp.getApplicationPalette().getAccentColor());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                activity,
                LayoutUtils.isLandscape(activity) ? 5 : 3)
        );
        mDialog.setContentView(view);

        mDialog.setTitle(R.string.online_users);
        mDialog.setOwnerActivity(activity);
    }

    public UserListDialog setOnUserClickListener(OnUserClickListener listener) {
        mClickListener = listener;
        return this;
    }

    public void show(List<String> data) {
        mRecyclerView.setAdapter(new UserListAdapter(data, this));
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        mDialog.dismiss();
    }

    @Override
    public void onUserClick(String nickname, boolean isLongClick) {
        if (mClickListener != null) {
            mDialog.dismiss();
            mClickListener.onUserClick(nickname, isLongClick);
        }
    }
}
