package corem.eldad.molestrike;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by The Gate Keeper on 1/15/2017.
 */

public class Settings extends PreferenceActivity {

    static PrefsFragment pf;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings.PrefsFragment()).commit();
        }
        initPref("display_name");
        initPref("music");
        initPref("soundFX");
        initPref("themes");
//        setContentView(R.layout.activity_settings);
    }

    private void initPref(String prefKey) {
        Preference preference = new Preference(this.getBaseContext());
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

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.my_preferences);
        }
    }
}
