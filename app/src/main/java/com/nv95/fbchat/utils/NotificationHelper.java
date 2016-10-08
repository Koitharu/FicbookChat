package com.nv95.fbchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.R;
import com.nv95.fbchat.core.ChatMessage;

/**
 * Created by nv95 on 20.08.16.
 */
public class NotificationHelper implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int POPUP_NONE = 0;
    private static final int POPUP_ME_ONLY = 1;
    private static final int POPUP_ALL = 2;

    private final Context mContext;
    private final Vibrator mVibrator;
    private int mPopupNotification;
    private Ringtone mSound;
    private boolean mVibration;

    public NotificationHelper(Context context) {
        mContext = context;
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPopupNotification = Integer.parseInt(prefs.getString("notify.popup", String.valueOf(POPUP_ME_ONLY)));
        mVibration = prefs.getBoolean("notify.vibrate", false);
        String sound = prefs.getString("notify.sound", "");
        mSound = TextUtils.isEmpty(sound) ? null : RingtoneManager.getRingtone(mContext, Uri.parse(sound));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        switch (key) {
            case "notify.popup":
                mPopupNotification = Integer.parseInt(prefs.getString("notify.popup", String.valueOf(POPUP_ME_ONLY)));
                break;
            case "notify.vibrate":
                mVibration = prefs.getBoolean("notify.vibrate", false);
                break;
            case "notify.sound":
                String sound = prefs.getString("notify.sound", "");
                mSound = RingtoneManager.getRingtone(mContext, Uri.parse(sound));
                break;
        }
    }

    public void notify(ChatMessage message, boolean mention) {
        if (mPopupNotification == POPUP_ALL || (mPopupNotification == POPUP_ME_ONLY && mention)) {
            toastPopup(message);
        }
        if (mSound != null && mention) {
            if (mSound.isPlaying()) {
                mSound.stop();
            }
            mSound.play();
        }
        if (mVibration && mention) {
            mVibrator.vibrate(100);
        }
    }

    private void toastPopup(ChatMessage msg) {
        DayNightPalette palette = ChatApp.getApplicationPalette();
        View v = View.inflate(mContext, R.layout.toast_popup, null);
        v.setBackgroundColor(ContextCompat.getColor(mContext, palette.isDark() ? R.color.black_80 : R.color.white_80));
        AvatarUtils.assignAvatarTo((ImageView) v.findViewById(R.id.imageViewAvatar), msg.login);
        TextView tv = ((TextView)v.findViewById(R.id.textViewLogin));
        tv.setTextColor(palette.getContrastColor());
        tv.setText(msg.login);
        tv = ((TextView)v.findViewById(R.id.textViewMessage));
        tv.setTextColor(palette.getContrastColor());
        tv.setText(msg.message);
        Toast toast = new Toast(mContext);
        toast.setView(v);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


}
