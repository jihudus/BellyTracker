package ru.kurant.bellytracker.controller.share;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.ui.DrinkListFragment;
import ru.kurant.bellytracker.ui.MainActivity;

public class Preferences extends PreferenceFragmentCompat {

    private ListPreference dateFormat;
    private ListPreference timeFormat;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            dateFormat.setSummary(dateFormat.getEntry());
            timeFormat.setSummary(timeFormat.getEntry());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity activity = (MainActivity) getActivity();
                activity.finish();
                activity.startActivity(Intent.makeMainActivity(activity.getComponentName()));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        dateFormat = findPreference(getString(R.string.date_pattern));
        CharSequence[] formats = getResources().getStringArray(R.array.date_formats);
        CharSequence[] formatViews = getResources().getStringArray(R.array.date_format_view);
        dateFormat.setEntries(formatViews);
        dateFormat.setEntryValues(formats);
        dateFormat.setSummary(dateFormat.getEntry());

        timeFormat = findPreference(getString(R.string.time_pattern));
        CharSequence[] timeFormats = getResources().getStringArray(R.array.time_formats);
        CharSequence[] timeFormatViews = getResources().getStringArray(R.array.time_format_view);
        timeFormat.setEntries(timeFormatViews);
        timeFormat.setEntryValues(timeFormats);
        timeFormat.setSummary(timeFormat.getEntry());
    }

    public SharedPreferences.OnSharedPreferenceChangeListener getPrefListener() {
        return prefListener;
    }
}
