package x_systems.x_messenger.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import x_systems.x_messenger.R;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class SettingsPreference extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        final static String KEY_NOTE = "note";
        final static String KEY_SUBJECT = "subject";
        final static String KEY_THEME = "theme";
        final static String KEY_MSGTIME = "msgtime";

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_settings);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Preference preference = findPreference(key);
            switch (key)
            {
                case KEY_NOTE:
                    break;
                case KEY_MSGTIME:
                    break;
                case KEY_SUBJECT:
                    preference.setSummary(sharedPreferences.getString(key, ""));
                    break;
                case KEY_THEME:
                    getActivity().recreate();
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }
}
