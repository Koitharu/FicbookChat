package com.nv95.fbchat;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchat.dialogs.OnUserClickListener;
import com.nv95.fbchat.utils.AvatarUtils;

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
        setHasStableIds(true);
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user_grid,
                parent,
                false
        ), mClickListener);
    }

    public void updateList(List<String> newDataset) {
        mDataset.clear();
        mDataset.addAll(newDataset);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        if (!mDataset.get(position).equals(holder.nickname)) {
            holder.nickname = mDataset.get(position);
            holder.textView.setText(holder.nickname);
            AvatarUtils.assignAvatarTo(holder.imageView, holder.nickname);
        }
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
