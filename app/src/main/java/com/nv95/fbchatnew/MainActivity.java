package com.nv95.fbchatnew;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nv95.fbchatnew.components.EndlessHeaderedAdapter;
import com.nv95.fbchatnew.core.AccountStore;
import com.nv95.fbchatnew.core.ChatCallback;
import com.nv95.fbchatnew.core.ChatMessage;
import com.nv95.fbchatnew.core.Rooms;
import com.nv95.fbchatnew.core.emoji.EmojiAdapter;
import com.nv95.fbchatnew.core.emoji.EmojiUtils;
import com.nv95.fbchatnew.core.emoji.OnEmojiSelectListener;
import com.nv95.fbchatnew.utils.AvatarUtils;
import com.nv95.fbchatnew.utils.CloseHelper;
import com.nv95.fbchatnew.utils.LayoutUtils;
import com.nv95.fbchatnew.utils.Palette;

import java.util.List;
import java.util.Random;

public class MainActivity extends BaseAppActivity implements TextWatcher, ServiceConnection, ChatCallback,
        LoginDialog.OnLoginListener, DialogInterface.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, EndlessHeaderedAdapter.OnLoadMoreListener, OnEmojiSelectListener, DialogInterface.OnCancelListener {

    private static final int REQUEST_SETTINGS = 238;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewEmoji;
    private ImageView mImageViewAvatar;
    private TextView mTextViewLogin;
    private ProgressBar mProgressBar;
    private NavigationView mNavigationView;
    private FloatingActionButton mFabSend;
    private EditText mEditTextMessage;
    private DrawerLayout mDrawerLayout;
    private ImageButton mImageButtonEmoji;
    private ActionBarDrawerToggle mToggle;
    private ChatMessagesAdapter mAdapter;
    @Nullable
    private ProgressDialog mProgressDialog;
    @Nullable
    private ChatService.ChatBinder mChatBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerViewEmoji = (RecyclerView) findViewById(R.id.recyclerViewEmoji);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mEditTextMessage = (EditText) findViewById(R.id.editMessage);
        mImageButtonEmoji = (ImageButton) findViewById(R.id.buttonEmoji);
        mFabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdapter = new ChatMessagesAdapter(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mFabSend.setOnClickListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (mChatBinder != null) {
                    mChatBinder.requestRooms();
                }
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mToggle);
        mNavigationView.setNavigationItemSelectedListener(this);
        View v = mNavigationView.getHeaderView(0);
        mTextViewLogin = (TextView) v.findViewById(R.id.textViewLogin);
        mImageViewAvatar = (ImageView) v.findViewById(R.id.imageViewAvatar);
        mImageButtonEmoji.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        EmojiAdapter adapter = new EmojiAdapter(this);
        GridLayoutManager lm = new GridLayoutManager(this, LayoutUtils.getOptimalColumnsCount(getResources(), getResources().getDimensionPixelSize(R.dimen.emoji_size_large), 0));
        lm.setReverseLayout(true);
        mRecyclerViewEmoji.setLayoutManager(lm);
        mRecyclerViewEmoji.setAdapter(adapter);
        mEditTextMessage.addTextChangedListener(this);
        mAdapter.setOnLoadMoreListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), this);
        mProgressDialog.show();

        Intent serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);
        bindService(serviceIntent, this, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onPaletteChanged(Palette palette) {
        super.onPaletteChanged(palette);
        mNavigationView.getHeaderView(0).setBackgroundColor(palette.getDarkColor());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mChatBinder != null) {
            mChatBinder.terminate();
        }
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_online:
                if (mChatBinder != null) {
                    mProgressDialog = new ProgressDialog(this);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setMessage(getString(R.string.loading));
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setOnCancelListener(this);
                    mProgressDialog.show();
                    mChatBinder.requestRoomMembers(null);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mRecyclerViewEmoji.getVisibility() == View.VISIBLE) {
            onClick(mImageButtonEmoji);
        } else if (CloseHelper.tryClose(mRecyclerView)) {
            super.onBackPressed();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mEditTextMessage.getText().length() == 0 && Boolean.TRUE.equals(mEditTextMessage.getTag())) {
            mFabSend.hide();
            mEditTextMessage.setMinLines(1);
            mEditTextMessage.setTag(false);
        } else {
            mFabSend.show();
            mEditTextMessage.setMinLines(2);
            mEditTextMessage.setTag(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mChatBinder = (ChatService.ChatBinder) iBinder;
        mChatBinder.setCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mChatBinder = null;
    }

    @Override
    public void onConnected() {
        if (!AccountStore.isAuthorized(this)) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            new LoginDialog(this, this).show();
        } else {
            mChatBinder.signIn(
                    AccountStore.getLogin(this),
                    AccountStore.getPassword(this)
            );
        }
    }

    @Override
    public void onAuthorizationFailed(String reason) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        new LoginDialog(this,this).show(reason);
    }

    @Override
    public void onAuthorizationSuccessful(String login, String password) {
        AccountStore.write(this, login, password);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        mTextViewLogin.setText(login);
        AvatarUtils.assignAvatarTo(mImageViewAvatar, login);
        mChatBinder.requestRooms();
    }

    @Override
    public void onRoomsUpdated(Rooms rooms, String current) {
        SubMenu navMenu = mNavigationView.getMenu().findItem(R.id.nav_rooms).getSubMenu();
        navMenu.removeGroup(R.id.groupRooms);
        for (int i = 0; i < rooms.size(); i++) {
            navMenu.add(R.id.groupRooms, i, i, rooms.get(i).name)
                    .setChecked(rooms.get(i).name.equals(current))
                    .setActionView(createBadge(rooms.get(i).users));
        }
        navMenu.setGroupCheckable(R.id.groupRooms, true, true);
        if (current == null) {
            current = getSharedPreferences("chat", MODE_PRIVATE).getString("room_name", null);
            int t = current == null ? -1 : rooms.indexOf(current);
            if (t == -1) {
                t = new Random().nextInt(rooms.size());
            }
            MenuItem item = navMenu.getItem(t);
            item.setChecked(true);
            mProgressBar.setVisibility(View.VISIBLE);
            mAdapter.clearAllItems();
            setSubtitle(item.getTitle().toString());
            mChatBinder.joinRoom(item.getTitle().toString());
        }
    }

    @Override
    public void onMessageReceived(ChatMessage message) {
        mAdapter.appendSingleMessage(message);
        if (LayoutUtils.findFirstVisibleItemPosition(mRecyclerView) <= 2) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onHistoryReceived(List<ChatMessage> history, String room) {
        if (room.equals(mChatBinder.getCurrentRoomName())) {
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setLoadEnabled(history.size() != 0);
            if (mAdapter.appendHistory(history)) {
                mRecyclerView.scrollToPosition(0);
            }
            mAdapter.setLoaded();
        }
    }

    @Override
    public void onAlertMessage(String message, boolean error, boolean quit) {
        if (quit && mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        new AlertDialog.Builder(this).setCancelable(false)
                .setMessage(message)
                .setIcon(error ? android.R.drawable.ic_dialog_alert : android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.close, quit ? new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                } : null)
                .create()
                .show();
    }

    @Override
    public void onOnlineListReceived(String room, List<String> participants) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            new UserListDialog(this).show(participants);
        }
    }

    @Override
    public void onQuitByUser() {
        finish();
    }

    @Override
    public void onLogin(String email, String password) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.signingin));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), this);
        mProgressDialog.show();
        mChatBinder.signIn(email, password);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case DialogInterface.BUTTON_NEGATIVE:
                mChatBinder.terminate();
                finish();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
                break;
            default:
                SubMenu subMenu = mNavigationView.getMenu().findItem(R.id.nav_rooms).getSubMenu();
                MenuItem o;
                for (int i=0;i<subMenu.size();i++) {
                    o = subMenu.getItem(i);
                    if (o.getTitle().toString().equals(mChatBinder.getCurrentRoomName())) {
                        o.setChecked(false);
                        break;
                    }
                }
                mProgressBar.setVisibility(View.VISIBLE);
                mAdapter.clearAllItems();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                setSubtitle(item.getTitle().toString());
                item.setChecked(true);
                mChatBinder.joinRoom(item.getTitle().toString());
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEmoji:
                if (mRecyclerViewEmoji.getVisibility() == View.VISIBLE) {
                    mRecyclerViewEmoji.setVisibility(View.GONE);
                    mImageButtonEmoji.setImageResource(R.drawable.ic_insert_emoticon_black_24dp);
                } else {
                    mRecyclerViewEmoji.setVisibility(View.VISIBLE);
                    mImageButtonEmoji.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
            case R.id.fabSend:
                mChatBinder.sendMessage(mEditTextMessage.getText().toString());
                mEditTextMessage.getText().clear();
                break;
        }
    }

    private void setSubtitle(CharSequence scq) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(scq);
        }
    }

    @Override
    public void onLoadMore() {
        if (mChatBinder != null) {
            ChatMessage cm = mAdapter.getLatestMessage();
            if (cm != null) {
                mChatBinder.requestMessagesHistory(cm.timestamp, 20);
            } else {
                mAdapter.setLoadEnabled(false);
            }
        } else {
            mAdapter.setLoadEnabled(false);
        }
    }

    @Nullable
    private View createBadge(int count) {
        if (count == 0) {
            return null;
        }
        View v = View.inflate(this, R.layout.item_badge, null);
        ((TextView)v.findViewById(android.R.id.text1)).setText(String.valueOf(count));
        return v;
    }

    @Override
    public void onEmojiSelected(int index) {
        mEditTextMessage.getText().append(EmojiUtils.getEmojiString(this, index));
        onClick(mImageButtonEmoji);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        mProgressDialog = null;
    }
}
