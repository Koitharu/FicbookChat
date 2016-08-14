package com.nv95.fbchatnew;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by nv95 on 13.08.16.
 */

public class SettingsActivity extends BaseAppActivity implements Preference.OnPreferenceChangeListener {

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
                Snackbar.make(findViewById(R.id.toolbar), R.string.need_restart, Snackbar.LENGTH_SHORT).show();
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
            }
        }
    }
}
