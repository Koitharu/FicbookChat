package com.nv95.fbchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.nv95.fbchat.components.preferences.ImagePreference;
import com.nv95.fbchat.core.AccountStore;
import com.nv95.fbchat.utils.MediaUtils;
import com.nv95.fbchat.utils.PreferencesUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by nv95 on 13.08.16.
 */

public class SettingsActivity extends BaseAppActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private PreferenceFragment mPrefFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPrefFragment = new PrefFragment();
        mPrefFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction()
                .add(R.id.content, mPrefFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case "dark":
                ChatApp.getApplicationPalette().setDark((Boolean) o);
                requestRestart();
                return true;
            case "debug":
                requestRestart();
                return true;
            default:
                return false;
        }
    }

    private void requestRestart() {
        setSubtitle(R.string.need_restart);
        Intent intent = new Intent();
        intent.putExtra("restart", true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        switch (preference.getKey()) {
            case "logout":
                new AlertDialog.Builder(this)
                        .setMessage(R.string.logout_confirm)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AccountStore.clear(SettingsActivity.this);
                                preference.setEnabled(false);
                                requestRestart();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
                return true;
            case "wallpaper":
                Crop.pickImage(this);
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    String f = MediaUtils.getImageFile(this, data.getData());
                    f = TextUtils.isEmpty(f) ? String.valueOf(System.currentTimeMillis()) : new File(f).getName();
                    File dir = getExternalFilesDir("wallpaper");
                    if (dir == null) {
                        dir = getFilesDir();
                    }
                    Uri destination = Uri.fromFile(new File(dir, f));
                    Crop.of(data.getData(), destination)
                            .withAspect(getResources().getDisplayMetrics().widthPixels,
                                    getResources().getDisplayMetrics().heightPixels)
                            .start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    ImagePreference preference = (ImagePreference) mPrefFragment.findPreference("wallpaper");
                    preference.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class PrefFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Activity activity = getActivity();
            if (activity != null && activity instanceof Preference.OnPreferenceChangeListener) {
                findPreference("dark").setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) activity);
                findPreference("logout").setOnPreferenceClickListener((Preference.OnPreferenceClickListener) activity);
                findPreference("logout").setSummary(AccountStore.getLogin(activity));
                findPreference("debug").setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) activity);
                PreferencesUtils.bindPreferenceSummary((ListPreference) findPreference("notify.popup"));
                PreferencesUtils.bindPreferenceSummary((RingtonePreference) findPreference("notify.sound"));
                findPreference("wallpaper").setOnPreferenceClickListener((Preference.OnPreferenceClickListener) activity);
                PreferencesUtils.bindPreferenceSummary((ImagePreference) findPreference("wallpaper"));
            }
        }
    }
}
