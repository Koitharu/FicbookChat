package com.nv95.fbchat;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchat.core.ChatMessage;
import com.nv95.fbchat.utils.AutoLinkMovement;
import com.nv95.fbchat.utils.AvatarUtils;
import com.nv95.fbchat.utils.DayNightPalette;

import java.util.List;

/**
 * Created by nv95 on 24.08.16.
 */

public class SearchResultActivity extends BaseAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchres);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setSubtitle(getIntent().getStringExtra("query"));
        List<ChatMessage> messages = getIntent().getParcelableArrayListExtra("messages");
        recyclerView.setAdapter(new SearchResultsAdapter(messages));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SimpleMessageHolder> {

        private final List<ChatMessage> mDataset;

        public SearchResultsAdapter(List<ChatMessage> messages) {
            mDataset = messages;
        }

        @Override
        public SimpleMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleMessageHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_searchres,
                    parent,
                    false
            ));
        }

        @Override
        public void onBindViewHolder(SimpleMessageHolder holder, int position) {
            ChatMessage msg = mDataset.get(position);
            DayNightPalette palette = DayNightPalette.fromString(msg.login, ChatApp.getApplicationPalette().isDark());
            holder.textViewLogin.setText(msg.login);
            holder.textViewMessage.setText(msg.message);
            holder.textViewMessage.setLinkTextColor(palette.getContrastAccentColor());
            holder.messageBlock.setBackgroundColor(palette.getCompatColor());
            AvatarUtils.assignAvatarTo(holder.imageView, msg.login);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private static class SimpleMessageHolder extends RecyclerView.ViewHolder {

        final View messageBlock;
        final TextView textViewLogin;
        final TextView textViewMessage;
        final ImageView imageView;

        public SimpleMessageHolder(View itemView) {
            super(itemView);
            textViewLogin = (TextView) itemView.findViewById(R.id.textViewLogin);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            messageBlock = itemView.findViewById(R.id.blockMessage);
            textViewMessage.setMovementMethod(AutoLinkMovement.getInstance());
        }
    }
}
