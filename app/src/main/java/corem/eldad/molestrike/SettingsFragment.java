package corem.eldad.molestrike;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by The Gate Keeper on 1/16/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        addPreferencesFromResource(R.xml.my_preferences);

        initPref("display_name");
        initPref("music");
        initPref("soundFX");
        initPref("themes");

    }

    private void initPref(String prefKey) {
        Preference preference = findPreference(prefKey);
        preference.setOnPreferenceChangeListener(sPreferenceChangeListener);
        sPreferenceChangeListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "(not set yet)"));

    }

    private static Preference.OnPreferenceChangeListener
            sPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

}