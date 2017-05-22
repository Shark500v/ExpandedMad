package com.polito.madinblack.expandedmad.settings;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.polito.madinblack.expandedmad.R;

public class SettingsFragment extends PreferenceFragment {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("Silent");

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_fragmentsettings);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindPreferenceSummaryToValue(findPreference("pref_key_currency"));
        bindPreferenceSummaryToValue(findPreference("pref_key_language"));
        //bindPreferenceSummaryToValue(findPreference("pref_key_notifications_able"));
        bindPreferenceSummaryToValue(findPreference("pref_key_notifications_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("pref_key_notifications_vibrate"));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);

        if (key.equals("pref_key_currency")) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) pref;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else if (key.equals("pref_key_language")) {
            // Set summary to be the user-description for the selected value
            pref.setSummary(sharedPreferences.getString(key, ""));
        } else if (key.equals("pref_key_notifications_able")) {

        } else if (key.equals("pref_key_notifications_ringtone")) {
            // Set summary to be the user-description for the selected value
            pref.setSummary(sharedPreferences.getString(key, ""));
        } else if (key.equals("pref_key_notifications_vibrate")) {

        }
    }*/

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
