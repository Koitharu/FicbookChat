package com.nv95.fbchatnew;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.nv95.fbchatnew.core.AccountStore;

/**
 * Created by nv95 on 13.08.16.
 */

public class SettingsActivity extends BaseAppActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction()
                .add(R.id.content, new PrefFragment())
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
                Toast.makeText(this, R.string.need_restart, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
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
                                Toast.makeText(SettingsActivity.this, R.string.need_restart, Toast.LENGTH_SHORT).show();
                                preference.setEnabled(false);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
                return true;
            default:
                return false;
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
            }
        }
    }
}
