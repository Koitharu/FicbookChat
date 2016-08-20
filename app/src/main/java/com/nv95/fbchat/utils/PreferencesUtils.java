package com.nv95.fbchat.utils;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.RingtonePreference;

import com.nv95.fbchat.R;

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

    public static void bindPreferenceSummary(RingtonePreference ringtonePreference) {
        String rtUri = ringtonePreference.getSharedPreferences().getString(ringtonePreference.getKey(), "");
        Ringtone rt = RingtoneManager.getRingtone(ringtonePreference.getContext(), Uri.parse(rtUri));
        String summ = rt == null ? ringtonePreference.getContext().getString(R.string.no_sound) : rt.getTitle(ringtonePreference.getContext());
        ringtonePreference.setSummary(summ);
        ringtonePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Ringtone rt = RingtoneManager.getRingtone(preference.getContext(), Uri.parse((String) newValue));
                String summ = rt == null ? preference.getContext().getString(R.string.no_sound) : rt.getTitle(preference.getContext());
                preference.setSummary(summ);
                return true;
            }
        });
    }
}
