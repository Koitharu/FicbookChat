package com.nv95.fbchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nv95.fbchat.components.preferences.ImagePreference;
import com.nv95.fbchat.core.AccountStore;
import com.nv95.fbchat.utils.AvatarUtils;
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
            case "servip":
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
                return true;
            case "ccache":
                new CacheClearTask(preference).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
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
                PreferencesUtils.bindPreferenceSummary((ListPreference) findPreference("notify.popup"));
                PreferencesUtils.bindPreferenceSummary((RingtonePreference) findPreference("notify.sound"));
                findPreference("wallpaper").setOnPreferenceClickListener((Preference.OnPreferenceClickListener) activity);
                findPreference("ccache").setOnPreferenceClickListener((Preference.OnPreferenceClickListener) activity);
                PreferencesUtils.bindPreferenceSummary((ImagePreference) findPreference("wallpaper"));
                PreferencesUtils.bindPreferenceSummary((EditTextPreference) findPreference("servip"), (Preference.OnPreferenceChangeListener) activity);
            }

            new AsyncTask<Void, Void, Float>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    findPreference("ccache").setSummary(R.string.size_calculating);
                }

                @Override
                protected Float doInBackground(Void... params) {
                    try {
                        return com.nv95.fbchat.utils.StorageUtils.dirSize(getActivity().getExternalCacheDir()) / 1048576f;
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Float aFloat) {
                    super.onPostExecute(aFloat);
                    Preference preference = findPreference("ccache");
                    if (preference != null) {
                        preference.setSummary(String.format(preference.getContext().getString(R.string.cache_size),
                                aFloat == null ? 0 : aFloat));
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class CacheClearTask extends AsyncTask<Void, Void, Void> {
        private Preference preference;

        public CacheClearTask(Preference preference) {
            this.preference = preference;
        }

        @Override
        protected void onPreExecute() {
            preference.setSummary(R.string.cache_clearing);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            AvatarUtils.clearLinksCache();
            ImageLoader.getInstance().getMemoryCache().clear();
            com.nv95.fbchat.utils.StorageUtils.removeDir(preference.getContext().getCacheDir());
            com.nv95.fbchat.utils.StorageUtils.removeDir(preference.getContext().getExternalCacheDir());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            preference.setSummary(String.format(preference.getContext().getString(R.string.cache_size), 0f));
            super.onPostExecute(aVoid);
        }
    }
}
