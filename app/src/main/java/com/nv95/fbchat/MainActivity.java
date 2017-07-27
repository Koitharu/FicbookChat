package com.nv95.fbchat;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nv95.fbchat.components.EndlessHeaderedAdapter;
import com.nv95.fbchat.components.WallpaperView;
import com.nv95.fbchat.components.recycler.GridRecyclerView;
import com.nv95.fbchat.core.AccountStore;
import com.nv95.fbchat.core.ChatCallback;
import com.nv95.fbchat.core.ChatMessage;
import com.nv95.fbchat.core.Rooms;
import com.nv95.fbchat.core.emoji.EmojiAdapter;
import com.nv95.fbchat.core.emoji.OnEmojiSelectListener;
import com.nv95.fbchat.core.ficbook.FicbookConnection;
import com.nv95.fbchat.dialogs.AdminMenuDialog;
import com.nv95.fbchat.dialogs.BanLogDialog;
import com.nv95.fbchat.dialogs.EditTextDialog;
import com.nv95.fbchat.dialogs.LoginDialog;
import com.nv95.fbchat.dialogs.OnUserClickListener;
import com.nv95.fbchat.dialogs.UserPreviewDialog;
import com.nv95.fbchat.utils.AvatarUtils;
import com.nv95.fbchat.utils.CloseHelper;
import com.nv95.fbchat.utils.DayNightPalette;
import com.nv95.fbchat.utils.LayoutUtils;
import com.nv95.fbchat.utils.SpanUtils;
import com.nv95.fbchat.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseAppActivity implements TextWatcher, ServiceConnection, ChatCallback,
        LoginDialog.OnLoginListener, DialogInterface.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, EndlessHeaderedAdapter.OnLoadMoreListener, OnEmojiSelectListener, OnUserClickListener {

    private static final int REQUEST_SETTINGS = 238;
    private static final int REQUEST_SETTINGS_RESTART = 239;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewUsers;
    private GridRecyclerView mRecyclerViewEmoji;
    private ImageView mImageViewAvatar;
    private TextView mTextViewLogin;
    private NavigationView mNavigationView;
    private FloatingActionButton mFabSend;
    private EditText mEditTextMessage;
    private ImageView mImageViewPower;
    private WallpaperView mWallpaperView;
    private DrawerLayout mDrawerLayout;
    private ImageButton mImageButtonEmoji;
    private ActionBarDrawerToggle mToggle;
    private ChatMessagesAdapter mAdapter;
    private UserListAdapter mUsersAdapter;
    private TextView mTextViewAbout;
    @Nullable
    private ChatService.ChatBinder mChatBinder;
    //dialog
    private View mDialogView;
    private TextView mTextViewHolder;
    private ImageView mImageViewIcon;
    private ProgressBar mProgressBar;
    private Button mButtonDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerViewEmoji = findViewById(R.id.recyclerViewEmoji);
        mDialogView = findViewById(R.id.dialog);
        mTextViewHolder = findViewById(R.id.textViewHolder);
        mImageViewIcon = findViewById(R.id.imageViewIcon);
        mProgressBar = findViewById(R.id.progressBar);
        mButtonDismiss = findViewById(R.id.buttonDismiss);
        mWallpaperView = findViewById(R.id.wallpaper);
        mNavigationView = findViewById(R.id.navigationView);
        mEditTextMessage = findViewById(R.id.editMessage);
        mImageButtonEmoji = findViewById(R.id.buttonEmoji);
        mFabSend = findViewById(R.id.fabSend);
        mDrawerLayout = findViewById(R.id.drawer);
        mTextViewAbout = findViewById(R.id.textViewAbout);
        mRecyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdapter = new ChatMessagesAdapter(mRecyclerView, this);
        mRecyclerView.setAdapter(mAdapter);
        mFabSend.setOnClickListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (mChatBinder != null) {
                    if (drawerView instanceof CoordinatorLayout) {
                        mChatBinder.requestRoomMembers(null);
                    } else {
                        mChatBinder.requestRooms();
                    }
                }
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mToggle);
        mNavigationView.setNavigationItemSelectedListener(this);
        View v = mNavigationView.getHeaderView(0);
        mImageViewPower = v.findViewById(R.id.imageViewPower);
        mTextViewLogin = v.findViewById(R.id.textViewLogin);
        mImageViewAvatar = v.findViewById(R.id.imageViewAvatar);
        mImageButtonEmoji.setOnClickListener(this);
        mImageViewPower.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mSnackbar != null && LayoutUtils.findFirstVisibleItemPosition(mRecyclerView) <= 2) {
                    mSnackbar.dismiss();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        mUsersAdapter = new UserListAdapter(new ArrayList<String>(), this);
        mRecyclerViewUsers.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerViewUsers.setAdapter(mUsersAdapter);

        EmojiAdapter adapter = new EmojiAdapter(this);
        GridLayoutManager lm = new GridLayoutManager(this, getOptimalColumnsCountTablet(this, getResources().getDimensionPixelSize(R.dimen.emoji_size_large)));
        lm.setReverseLayout(true);
        mRecyclerViewEmoji.setLayoutManager(lm);
        mRecyclerViewEmoji.setAdapter(adapter);
        mEditTextMessage.addTextChangedListener(this);
        mAdapter.setOnLoadMoreListener(this);
        mWallpaperView.setWallpaper(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wallpaper", ""));

        doConnect();
    }

    private void doConnect() {
        if (!FicbookConnection.checkConnection(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.no_connection)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doConnect();
                        }
                    })
                    .setNegativeButton(R.string.exit, this)
                    .create().show();
            return;
        }

        dialog(R.string.connecting, 0, true, false);


        Intent serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);
        bindService(serviceIntent, this, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {
            if (data.hasExtra("restart") && data.getBooleanExtra("restart", false)) {
                ChatApp.restart(this);
            } else {
                mWallpaperView.setWallpaper(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wallpaper", ""));
            }
        } else if (requestCode == REQUEST_SETTINGS_RESTART) {
            ChatApp.restart(this);
        }
    }

    @Override
    public void onPaletteChanged(DayNightPalette palette) {
        super.onPaletteChanged(palette);
        mNavigationView.getHeaderView(0).setBackgroundColor(palette.getDarkColor());
        ColorStateList csl = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_enabled},
                new int[]{}
        }, new int[]{
                palette.getInverseColor(),
                palette.getGrayColor(),
                palette.getContrastColor()
        });
        mButtonDismiss.setTextColor(palette.getAccentColor());
        mNavigationView.setItemTextColor(csl);
        mNavigationView.setItemIconTintList(csl);
        ThemeUtils.setDrawableCompat(
                mImageButtonEmoji,
                mRecyclerViewEmoji.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_arrow_down_black_24dp : R.drawable.ic_insert_emoticon_black_24dp,
                ChatApp.getApplicationPalette()
        );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        if (mChatBinder != null) {
            mChatBinder.setBackground(true);
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mChatBinder != null) {
            mChatBinder.setBackground(false);
        }
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        if (mChatBinder != null) {
            mChatBinder.terminate();
            unbindService(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int online = mChatBinder != null ? mChatBinder.getCurrentOnline() : -1;
        menu.findItem(R.id.action_online).setIcon(
                SpanUtils.getCounterIcon(this, online > 0 ? String.valueOf(online) : "?")
        );
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_online:
                if (mChatBinder != null) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
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
        } else if (mButtonDismiss.isShown()) {
            hideDialog();
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
            hideDialog();
            new LoginDialog(this, this).show();
        } else {
            assert mChatBinder != null;
            mChatBinder.signIn(
                    AccountStore.getLogin(this),
                    AccountStore.getPassword(this)
            );
        }
    }

    @Override
    public void onAuthorizationFailed(String reason) {
        hideDialog();
        new LoginDialog(this,this).show(reason);
    }

    @Override
    public void onAuthorizationSuccessful(String login, String password, int power) {
        AccountStore.write(this, login, password);
        hideDialog();
        mTextViewLogin.setText(login);
        AvatarUtils.assignAvatarTo(mImageViewAvatar, login);
        assert mChatBinder != null;
        mChatBinder.requestRooms();
        if (power >= ChatService.POWER_ADMIN) {
            mImageViewPower.setImageResource(R.drawable.ic_icon_key_white);
            mImageViewPower.setVisibility(View.VISIBLE);
        } else if (power >= ChatService.POWER_MODER) {
            mImageViewPower.setImageResource(R.drawable.ic_icon_star_white);
            mImageViewPower.setVisibility(View.VISIBLE);
        } else {
            mImageViewPower.setVisibility(View.GONE);
        }
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
            assert mChatBinder != null;
            mChatBinder.joinRoom(item.getTitle().toString());
        }
    }

    @Override
    public void onMessageReceived(ChatMessage message) {
        mAdapter.appendSingleMessage(message);
        if (LayoutUtils.findFirstVisibleItemPosition(mRecyclerView) <= 2) {
            mRecyclerView.scrollToPosition(0);
        } else {
            updateBottomSnackbar();
        }
    }

    @Override
    public void onHistoryReceived(List<ChatMessage> history, String room) {
        assert mChatBinder != null;
        if (room.equals(mChatBinder.getCurrentRoomName())) {
            hideDialog();
            mAdapter.setLoadEnabled(history.size() != 0);
            if (mAdapter.appendHistory(history)) {
                mRecyclerView.scrollToPosition(0);
            }
            mAdapter.setLoaded();
        }
    }

    @Override
    public void onAlertMessage(String message, boolean error, boolean quit) {
        dialog(
                message,
                ContextCompat.getDrawable(this, error ? android.R.drawable.ic_dialog_alert : android.R.drawable.ic_dialog_info),
                false, !quit
        );
    }

    @Override
    public void onOnlineListReceived(String room, List<String> participants) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mUsersAdapter.updateList(participants);
        }
    }

    @Override
    public void onUserCountChanged(int count) {
        invalidateOptionsMenu();
    }

    @Override
    public void onQuitByUser() {
        finish();
    }

    @Override
    public void onSearchResult(ArrayList<ChatMessage> messages, String query) {
        if (!isDialog()) {
            return;
        }
        hideDialog();
        if (messages.size() == 0) {
            onAlertMessage(getString(R.string.nothing_found, query), false, false);
        } else {
            startActivity(new Intent(this, SearchResultActivity.class)
                    .putParcelableArrayListExtra("messages", messages)
                    .putExtra("query", query)
            );
        }
    }

    @Override
    public void onRoomInfo(String about) {
        mTextViewAbout.setText(TextUtils.isEmpty(about) ? getString(R.string.no_description) : about);
    }

    @Override
    public void onActiveBans(BanLogDialog.BanLogItem[] bans) {
        new BanLogDialog(this, bans).show();
    }

    @Override
    public void onLogin(String email, String password) {
        dialog(R.string.signingin, 0, true, false);
        assert mChatBinder != null;
        mChatBinder.signIn(email, password);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case DialogInterface.BUTTON_NEGATIVE:
                if (mChatBinder != null) {
                    mChatBinder.terminate();
                }
                finish();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                new EditTextDialog(this, R.string.search, new EditTextDialog.OnTextChangedListener() {
                    @Override
                    public void onTextChanged(String newText) {
                        dialog(R.string.loading, 0, true, true);
                        assert mChatBinder != null;
                        mChatBinder.search(newText);
                    }
                }).show(R.string.enter_query, null);
                break;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
                break;
            default:
                assert mChatBinder != null;
                if (!mChatBinder.isConnected() || item.getTitle().equals(mChatBinder.getCurrentRoomName())) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
                SubMenu subMenu = mNavigationView.getMenu().findItem(R.id.nav_rooms).getSubMenu();
                MenuItem o;
                for (int i=0;i<subMenu.size();i++) {
                    o = subMenu.getItem(i);
                    if (o.getTitle().toString().equals(mChatBinder.getCurrentRoomName())) {
                        o.setChecked(false);
                        break;
                    }
                }
                dialog(R.string.loading, 0, true, false);
                mAdapter.clearAllItems();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                setSubtitle(item.getTitle().toString());
                item.setChecked(true);
                mChatBinder.joinRoom(item.getTitle().toString());
                invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEmoji:
                if (mRecyclerViewEmoji.getVisibility() == View.VISIBLE) {
                    mRecyclerViewEmoji.setVisibility(View.GONE);
                    ThemeUtils.setDrawableCompat(mImageButtonEmoji, R.drawable.ic_insert_emoticon_black_24dp, ChatApp.getApplicationPalette());
                } else {
                    mRecyclerViewEmoji.setVisibility(View.VISIBLE);
                    mRecyclerViewEmoji.animateGrid();
                    ThemeUtils.setDrawableCompat(mImageButtonEmoji, R.drawable.ic_keyboard_arrow_down_black_24dp, ChatApp.getApplicationPalette());
                }
                break;
            case R.id.imageViewPower:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                new AdminMenuDialog(this, mChatBinder).show();
                break;
            case R.id.fabSend:
                String msg = mEditTextMessage.getText().toString().trim();
                if (msg.length() != 0 && mChatBinder != null) {
                    mChatBinder.sendMessage(msg);
                    mEditTextMessage.getText().clear();
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if ("wallpaper".equals(s)) {
            mWallpaperView.setWallpaper(sharedPreferences.getString("wallpaper", ""));
        } else {
            super.onSharedPreferenceChanged(sharedPreferences, s);
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
        mEditTextMessage.getText().append(SpanUtils.getEmojiString(this, index));
        onClick(mImageButtonEmoji);
    }

    @Override
    public void onUserClick(String nickname, boolean isLongClick) {
        if (isLongClick) {
            new UserPreviewDialog(this, mChatBinder).show(nickname);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mEditTextMessage.getText().insert(0,  " ");
            mEditTextMessage.getText().insert(0, SpanUtils.getUserString(this, nickname));
        }
    }

    public static int getOptimalColumnsCountTablet(Context context, int columnWidth) {
        float width = LayoutUtils.isTabletLandscape(context) ? LayoutUtils.DpToPx(context.getResources(), 404) : context.getResources().getDisplayMetrics().widthPixels;

        float modW = width % columnWidth;
        int count = (int) (width / columnWidth);
        if (modW > columnWidth/2) {
            count++;
        }
        if (count == 0) {
            count = 1;
        }
        return count;
    }

    @Nullable
    private Snackbar mSnackbar = null;
    private int mCounter = 0;

    public void updateBottomSnackbar() {
        mCounter++;
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mRecyclerView, getResources().getQuantityString(R.plurals.new_messages, mCounter, mCounter), Snackbar.LENGTH_INDEFINITE);
            mSnackbar.setAction(R.string.down, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerView.scrollToPosition(0);
                }
            });
            mSnackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    mSnackbar = null;
                    mCounter = 0;
                    super.onDismissed(snackbar, event);
                }
            });
            mSnackbar.show();
        } else {
            mSnackbar.setText(getResources().getQuantityString(R.plurals.new_messages, mCounter, mCounter));
        }
    }

    private void hideDialog() {
        mDialogView.setVisibility(View.GONE);
        mRecyclerView.setEnabled(true);
        mRecyclerView.setAlpha(1f);
        mRecyclerView.setClickable(true);
        mEditTextMessage.setEnabled(true);
        mRecyclerView.setFocusable(true);
        if (mEditTextMessage.getText().length() != 0) {
            mFabSend.show();
        }
    }

    boolean isDialog() {
        return mDialogView.isShown();
    }

    private void dialog(@StringRes int text, @DrawableRes int icon, boolean showWait, boolean dismissible) {
        dialog(
                text == 0 ? null : getString(text),
                icon == 0 ? null : ContextCompat.getDrawable(this, icon),
                showWait,
                dismissible
        );
    }

    private void dialog(@Nullable CharSequence text, @Nullable Drawable icon, boolean showWait, boolean dismissible) {
        if (TextUtils.isEmpty(text)) {
            mTextViewHolder.setVisibility(View.GONE);
        } else {
            mTextViewHolder.setText(text);
            mTextViewHolder.setVisibility(View.VISIBLE);
        }
        if (icon == null) {
            mImageViewIcon.setVisibility(View.GONE);
        } else {
            mImageViewIcon.setImageDrawable(icon);
            mImageViewIcon.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(showWait ? View.VISIBLE : View.GONE);
        mButtonDismiss.setVisibility(dismissible ? View.VISIBLE : View.GONE);
        mDialogView.setVisibility(View.VISIBLE);
        mRecyclerView.setEnabled(false);
        mEditTextMessage.setEnabled(false);
        mRecyclerView.setAlpha(0.4f);
        mRecyclerView.setClickable(false);
        mRecyclerView.setFocusable(false);
        mFabSend.hide();
    }
}
