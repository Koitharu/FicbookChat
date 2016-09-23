package com.nv95.fbchat.utils;

import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.nv95.fbchat.R;
import com.nv95.fbchat.components.preferences.ImagePreference;

import java.io.File;

/**
 * Created by nv95 on 20.08.16.
 */

public class PreferencesUtils {

    public static void bindPreferenceSummary(ListPreference listPreference) {
        int index = listPreference.findIndexOfValue(listPreference.getValue());
        String summ = listPreference.getEntries()[index].toString();
        listPreference.setSummary(summ);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = ((ListPreference) preference).findIndexOfValue((String) newValue);
                String summ = ((ListPreference) preference).getEntries()[index].toString();
                preference.setSummary(summ);
                return true;
            }
        });
    }

    public static void bindPreferenceSummary(EditTextPreference editTextPreference) {
        String summ = editTextPreference.getText();
        editTextPreference.setSummary(summ);
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });
    }

    public static void bindPreferenceSummary(ImagePreference imagePreference) {
        File f = imagePreference.getFile();
        String summ = f == null ? imagePreference.getContext().getString(R.string.no_image) : f.getName();
        imagePreference.setSummary(summ);
        imagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (TextUtils.isEmpty((String) newValue)) {
                    preference.setSummary(R.string.no_image);
                } else {
                    preference.setSummary(new File((String) newValue).getName());
                }
                return true;
            }
        });
    }

    public static void bindPreferenceSummary(RingtonePreference ringtonePreference) {
        String rtUri = ringtonePreference.getSharedPreferences().getString(ringtonePreference.getKey(), "");
        String summ = TextUtils.isEmpty(rtUri) ? ringtonePreference.getContext().getString(R.string.no_sound)
                : RingtoneManager.getRingtone(ringtonePreference.getContext(), Uri.parse(rtUri)).getTitle(ringtonePreference.getContext());
        ringtonePreference.setSummary(summ);
        ringtonePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summ = TextUtils.isEmpty((String)newValue) ? preference.getContext().getString(R.string.no_sound)
                        : RingtoneManager.getRingtone(preference.getContext(), Uri.parse((String) newValue)).getTitle(preference.getContext());
                preference.setSummary(summ);
                return true;
            }
        });
    }
}
