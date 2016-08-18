package com.nv95.fbchat.core.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nv95.fbchat.R;

/**
 * Created by nv95 on 08.08.16.
 */

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiHolder> {

    private final OnEmojiSelectListener mSelectListener;
    private int size = 0;

    public EmojiAdapter(OnEmojiSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    @Override
    public EmojiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (size == 0) {
             size = parent.getResources().getDimensionPixelSize(R.dimen.emoji_size_large);
        }
        return new EmojiHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji, parent, false), mSelectListener);
    }

    @Override
    public void onBindViewHolder(EmojiHolder holder, int position) {
        ((ImageView)holder.itemView).setImageBitmap(EmojiCompat.getEmojiBitmap(holder.getContext(), position, size));
    }

    @Override
    public int getItemCount() {
        return EmojiCompat.getCount();
    }

    static class EmojiHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnEmojiSelectListener mSelectListener;

        public EmojiHolder(View itemView, OnEmojiSelectListener selectListener) {
            super(itemView);
            mSelectListener = selectListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectListener.onEmojiSelected(getAdapterPosition());
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
