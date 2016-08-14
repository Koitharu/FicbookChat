package com.nv95.fbchatnew;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchatnew.dialogs.OnUserClickListener;
import com.nv95.fbchatnew.utils.AvatarUtils;

import java.util.List;

/**
 * Created by nv95 on 12.08.16.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private final List<String> mDataset;
    @Nullable
    private final OnUserClickListener mClickListener;

    public UserListAdapter(List<String> dataset, @Nullable OnUserClickListener listener) {
        mClickListener = listener;
        mDataset = dataset;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user_grid,
                parent,
                false
        ), mClickListener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.textView.setText(holder.nickname = mDataset.get(position));
        AvatarUtils.assignAvatarTo(holder.imageView, mDataset.get(position));
    }

    static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        String nickname;
        final TextView textView;
        final ImageView imageView;
        private final OnUserClickListener mClickListener;

        public UserHolder(View itemView, OnUserClickListener clickListener) {
            super(itemView);
            mClickListener = clickListener;
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            if (clickListener != null) {
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            mClickListener.onUserClick(nickname, false);
        }

        @Override
        public boolean onLongClick(View view) {
            mClickListener.onUserClick(nickname, true);
            return true;
        }
    }
}
