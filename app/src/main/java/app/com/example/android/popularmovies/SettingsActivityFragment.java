package app.com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String LOG_TAG = SettingsActivityFragment.class.getSimpleName();

    public SettingsActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Map<String, ?> allPrefs = sharedPreferences.getAll();
        for (Map.Entry<String, ?> pref : allPrefs.entrySet()) {
            String key = pref.getKey();
            updatePref(findPreference(key), key);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePref(findPreference(key), key);
    }

    private void updatePref(Preference preference, String key){
        if (preference == null) {
            return;
        }
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        }
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        String value = sharedPreferences.getString(key, "none selected");
        preference.setSummary(value);
    }
}
