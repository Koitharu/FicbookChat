package com.nv95.fbchatnew.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.ChatService;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.components.AvatarBehavior;
import com.nv95.fbchatnew.core.ficbook.ProfileParser;
import com.nv95.fbchatnew.utils.AutoLinkMovement;
import com.nv95.fbchatnew.utils.AvatarUtils;
import com.nv95.fbchatnew.utils.DayNightPalette;
import com.nv95.fbchatnew.utils.ThemeUtils;

/**
 * Created by nv95 on 16.08.16.
 */

public class UserPreviewDialog implements DialogInterface.OnDismissListener, Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private final AlertDialog mDialog;
    private String mUsername;
    private final ChatService.ChatBinder mBinder;
    private final View mContentView;
    @Nullable
    private LoadTask mTask;

    private final TextView mTextViewContent;
    private final ProgressBar mProgressBar;
    private final ImageView mImageViewAvatar;
    private final Toolbar mToolbar;

    public UserPreviewDialog(Activity activity, ChatService.ChatBinder binder) {
        mBinder = binder;
        mContentView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_profile, null, false);
        mTextViewContent = (TextView) mContentView.findViewById(R.id.textViewContent);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);
        mToolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);
        mImageViewAvatar = (ImageView) mContentView.findViewById(R.id.imageViewAvatar);
        mTextViewContent.setMovementMethod(AutoLinkMovement.getInstance());

        AvatarBehavior.link(
                (AppBarLayout) mContentView.findViewById(R.id.appbar_container),
                mImageViewAvatar
        );

        mToolbar.inflateMenu(R.menu.profile);
        mToolbar.getMenu().findItem(R.id.action_banhammer).setVisible(
                binder.isModer()
        );
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setOnMenuItemClickListener(this);

        mDialog = new AlertDialog.Builder(activity)
                .setOnDismissListener(this)
                .setView(mContentView)
                .create();
        mDialog.setOwnerActivity(activity);
    }

    public void show(String nickname) {
        mUsername = nickname;
        mToolbar.setTitle(mUsername);
        AvatarUtils.assignAvatarTo(mImageViewAvatar, mUsername);
        DayNightPalette palette = DayNightPalette.fromString(nickname, ChatApp.getApplicationPalette().isDark());
        ThemeUtils.paintView(mContentView, palette);
        mTextViewContent.setLinkTextColor(palette.getAccentColor());
        palette.setAlpha(0.6f);
        if (nickname.equals(mBinder.getMe())) {
            mToolbar.getMenu().findItem(R.id.action_banhammer).setVisible(false);
        }
        mDialog.show();
        new LoadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUsername);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_banhammer:
                if (mBinder.isAdmin()) {
                    new BanhammerDialog(mDialog.getOwnerActivity(), mBinder)
                            .show(mUsername);
                } else if (mBinder.isModer()) {
                    BanhammerDialog.kikDialog(mDialog.getOwnerActivity(), mUsername, mBinder);
                }
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        mDialog.dismiss();
    }

    private class LoadTask extends AsyncTask<String,Void,ProfileParser> {

        public LoadTask() {
            mTask = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ProfileParser doInBackground(String... strings) {
            return ProfileParser.fromName(strings[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
        }

        @Override
        protected void onPostExecute(ProfileParser profileParser) {
            super.onPostExecute(profileParser);
            mTask = null;
            FloatingActionButton fab = (FloatingActionButton) mContentView.findViewById(R.id.fab);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (profileParser.isSuccess()) {
                CharSequence s = profileParser.getUserInfo();
                if (TextUtils.isEmpty(s)) {
                    mTextViewContent.setText(R.string.no_profile_info);
                } else {
                    mTextViewContent.setText(profileParser.getUserInfo());
                }
                final String link = profileParser.getProfileLink();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    }
                });
            } else {
                mTextViewContent.setText(R.string.profile_info_fail);
                fab.hide();
            }
        }
    }
}
