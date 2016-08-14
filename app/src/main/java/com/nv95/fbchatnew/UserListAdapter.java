package com.nv95.fbchatnew;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchatnew.utils.AvatarUtils;

import java.util.List;

/**
 * Created by nv95 on 12.08.16.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private final List<String> mDataset;

    public UserListAdapter(List<String> dataset) {
        this.mDataset = dataset;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user_grid,
                parent,
                false
        ));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.textView.setText(mDataset.get(position));
        AvatarUtils.assignAvatarTo(holder.imageView, mDataset.get(position));
    }

    static class UserHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final ImageView imageView;

        public UserHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
