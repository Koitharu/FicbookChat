package com.nv95.fbchatnew;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv95.fbchatnew.components.BubbleDrawable;
import com.nv95.fbchatnew.components.EndlessHeaderedAdapter;
import com.nv95.fbchatnew.core.ChatMessage;
import com.nv95.fbchatnew.dialogs.OnUserClickListener;
import com.nv95.fbchatnew.utils.AutoLinkMovement;
import com.nv95.fbchatnew.utils.AvatarUtils;
import com.nv95.fbchatnew.utils.DayNightPalette;
import com.nv95.fbchatnew.utils.TimestampUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nv95 on 11.08.16.
 */

public class ChatMessagesAdapter extends EndlessHeaderedAdapter<RecyclerView.ViewHolder> {

    private final LinkedList<ChatMessage> mDataset;
    @Nullable
    private final OnUserClickListener mUserClickListener;

    public ChatMessagesAdapter(RecyclerView recyclerView, @Nullable OnUserClickListener clickListener) {
        super(recyclerView);
        mDataset = new LinkedList<>();
        mUserClickListener = clickListener;
    }

    @Override
    public int getDataItemCount() {
        return mDataset.size();
    }

    @Override
    public int getDataItemType(int position) {
        return mDataset.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.MSG_EVENT) {
            return new EventHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_event,
                    parent,
                    false
            ));
        }
        MessageHolder mh = new MessageHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        viewType == ChatMessage.MSG_MY ? R.layout.item_message_my : R.layout.item_message,
                        parent,
                        false
                ), mUserClickListener);
        mh.bubble.setRtl(viewType == ChatMessage.MSG_MY);
        return mh;
    }

    @Override
    public void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage cm = mDataset.get(position);
        if (holder instanceof MessageHolder) {
            ((MessageHolder)holder).bubble.setColor(DayNightPalette.fromString(cm.login, ChatApp.getApplicationPalette().isDark()).getCompatColor());
            ((MessageHolder)holder).textViewLogin.setText(cm.login);
            ((MessageHolder)holder).textViewMessage.setText(cm.message);
            AvatarUtils.assignAvatarTo(((MessageHolder)holder).imageView, cm.login);
            if (position == mDataset.size() - 1 || TimestampUtils.getDiffMinutes(mDataset.get(position + 1).timestamp, cm.timestamp) >= 20) {
                ((MessageHolder)holder).textViewHeader.setVisibility(View.VISIBLE);
                ((MessageHolder)holder).textViewHeader.setText(TimestampUtils.formatRelative(cm.timestamp));
            } else {
                ((MessageHolder)holder).textViewHeader.setVisibility(View.GONE);
            }
        } else if (holder instanceof EventHolder) {
            ((EventHolder)holder).textViewLogin.setText(cm.login);
            ((EventHolder)holder).textViewMessage.setText(cm.message);
            AvatarUtils.assignAvatarTo(((EventHolder)holder).imageView, cm.login);
            ((ViewGroup)holder.itemView).getChildAt(0).requestLayout();
        }
    }


    public void clearAllItems() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public boolean appendHistory(List<ChatMessage> history) {
        int sz = mDataset.size();
        mDataset.addAll(sz, history);
        notifyItemRangeInserted(sz, history.size());
        return sz == 0;
    }

    public void appendSingleMessage(ChatMessage msg) {
        mDataset.add(0, msg);
        notifyItemInserted(getHeadersCount());
    }

    @Nullable
    public ChatMessage getLatestMessage() {
        return mDataset.getLast();
    }

    private static class MessageHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        final LinearLayout blockMessage;
        final TextView textViewLogin;
        final TextView textViewMessage;
        final BubbleDrawable bubble;
        final TextView textViewHeader;
        final ImageView imageView;
        @Nullable
        private final OnUserClickListener mClickListener;

        MessageHolder(View itemView, @Nullable OnUserClickListener clickListener) {
            super(itemView);
            mClickListener = clickListener;
            blockMessage = (LinearLayout) itemView.findViewById(R.id.blockMessage);
            textViewLogin = (TextView) itemView.findViewById(R.id.textViewLogin);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewHeader = (TextView) itemView.findViewById(R.id.textViewHeader);
            blockMessage.setBackgroundDrawable(bubble = new BubbleDrawable());
            textViewMessage.setMovementMethod(AutoLinkMovement.getInstance());
            imageView.setOnClickListener(this);
            imageView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {
                mClickListener.onUserClick(textViewLogin.getText().toString(), true);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onUserClick(textViewLogin.getText().toString(), false);
            }
        }
    }

    private static class EventHolder extends RecyclerView.ViewHolder {

        final TextView textViewLogin;
        final TextView textViewMessage;
        final ImageView imageView;

        EventHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), ChatApp.getApplicationPalette().isDark() ? R.color.white_60 : R.color.black_60));
            textViewLogin = (TextView) itemView.findViewById(R.id.textViewLogin);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
