package com.nv95.fbchat;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
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
import com.nv95.fbchat.utils.SpanUtils;

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
        String query = getIntent().getStringExtra("query");
        setSubtitle(query);
        List<ChatMessage> messages = getIntent().getParcelableArrayListExtra("messages");
        for (ChatMessage o : messages) {
            o.message = makeSpans(o.message.toString(), query.toLowerCase());
        }
        recyclerView.setAdapter(new SearchResultsAdapter(messages));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence makeSpans(String message, String what) {
        CharSequence cs = SpanUtils.makeSpans(this, message, null);
        int color = ChatApp.getApplicationPalette().getGrayColor();
        SpannableString ss = new SpannableString(cs);
        for (int i=0;i<ss.length() - what.length() + 1;i++) {
            if (ss.subSequence(i, i + what.length()).toString().toLowerCase().equals(what)) {
                ss.setSpan(new BackgroundColorSpan(color), i, i + what.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i += what.length();
            }
        }
        return ss;
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
