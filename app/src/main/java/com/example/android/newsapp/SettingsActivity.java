package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private String mCategory;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference searchQuery = findPreference(getString(R.string.settings_query_key));
            bindPreferenceSummaryToValue(searchQuery);

            mCategory = getText(R.string.settings_category_key).toString();
            final MultiSelectListPreference multiPref = (MultiSelectListPreference) findPreference(mCategory);
            multiPref.setOnPreferenceChangeListener(this);

            Preference category  = findPreference(getString(R.string.settings_category_key));
            bindPreferenceSummaryToValueHash(category);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String key = preference.getKey();

            String stringValue = value.toString();
            preference.setSummary(stringValue);

            //apparently that part is responsible for not refreshing ordering 2nd item name in settings tab
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex > 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } if (preference instanceof MultiSelectListPreference) {
                List<String> newValues = new ArrayList<>((HashSet<String>) value);

                preference.setSummary(TextUtils.join(", ", getSummaryListFromValueList(newValues)));
            }

            else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        private void bindPreferenceSummaryToValueHash (Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            Set<String> hs = preferences.getStringSet("set", new HashSet<String>());
            Set<String> in = new HashSet<String>(hs);
            Set preferenceString = preferences.getStringSet(preference.getKey(), in);
            onPreferenceChange(preference, preferenceString);
        }

        private List<String> getSummaryListFromValueList(List<String> valueList) {
            String[] allSummaries = getResources().getStringArray(R.array.settings_category_labels);
            String[] allValues = getResources().getStringArray(R.array.settings_category_values);

            List<String> summaryList = new ArrayList<>();
            for (int i = 0; i < allValues.length; i++) {
                for (String value : valueList) {
                    if (allValues[i].equals(value)) {
                        summaryList.add(allSummaries[i]);
                    }
                }
            }
            return summaryList;
        }
    }
}