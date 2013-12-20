/*
 * Copyright (C) 2013 Mudar Noufal, PeaceOfMind+
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.mudar.fairphone.peaceofmind.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.List;

import ca.mudar.fairphone.peaceofmind.R;

import static ca.mudar.fairphone.peaceofmind.data.PeaceOfMindPrefs.PrefsNames;
import static ca.mudar.fairphone.peaceofmind.data.PeaceOfMindPrefs.PrefsValues;

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.activity_settings);

        showBreadCrumbs(getResources().getString(R.string.prefs_breadcrumb), null);
    }

    @Override
    public Intent getIntent() {
        // Override the original intent to remove headers and directly show SettingsFragment
        final Intent intent = new Intent(super.getIntent());
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        return intent;
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class SettingsFragment extends PreferenceFragment implements
            OnSharedPreferenceChangeListener {

        protected SharedPreferences mSharedPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        }

        @Override
        public void onResume() {
            super.onResume();

            /**
             * Set up a listener whenever a key changes
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
            }

            final Preference maxDuration = findPreference(PrefsNames.MAX_DURATION);
            maxDuration.setSummary(getMaxDurationSummary());
        }

        @Override
        public void onPause() {
            /**
             * Remove the listener onPause
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
            }

            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            /**
             * onChanged, new preferences values are sent to the AppHelper.
             */
            if (key.equals(PrefsNames.MAX_DURATION)) {
                final Preference maxDuration = findPreference(PrefsNames.MAX_DURATION);
                maxDuration.setSummary(getMaxDurationSummary());
            }
        }

        private int getMaxDurationSummary() {
            final String value = mSharedPrefs.getString(PrefsNames.MAX_DURATION, PrefsValues.DELAY_FAST);
            int res;
            if (value.equals(PrefsValues.DELAY_SLOW)) {
                res = R.string.prefs_duration_slow;
            } else if (value.equals(PrefsValues.DELAY_MODERATE)) {
                res = R.string.prefs_duration_moderate;
            } else {
                res = R.string.prefs_duration_fast;
            }

            return res;
        }
    }
}
