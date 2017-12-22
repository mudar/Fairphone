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

package ca.mudar.fairphone.peaceofmind.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import ca.mudar.fairphone.peaceofmind.BuildConfig
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.PrefsNames
import ca.mudar.fairphone.peaceofmind.Const.PrefsValues
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.ui.activity.AboutActivity
import ca.mudar.fairphone.peaceofmind.util.LogUtils


class SettingsFragment : PreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private var durationPref: Preference? = null

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager.sharedPreferencesName = Const.APP_PREFS_NAME
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        addPreferencesFromResource(R.xml.preferences)

        durationPref = findPreference(PrefsNames.MAX_DURATION)

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        findPreference(PrefsNames.ABOUT)?.onPreferenceClickListener = this
        findPreference(PrefsNames.NOTIFICATION_ACCESS)?.onPreferenceClickListener = this
    }

    override fun onResume() {
        super.onResume()

        setupSummaries()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove the listener
        preferenceManager?.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Implements SharedPreferences.OnSharedPreferenceChangeListener
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PrefsNames.MAX_DURATION) {
            durationPref?.summary = getMaxDurationSummary()
        }
    }

    /**
     * Implements Preference.OnPreferenceClickListener
     */
    override fun onPreferenceClick(preference: Preference?): Boolean {
        val key = preference?.key
        return when (key) {
            PrefsNames.ABOUT -> {
                startActivity(AboutActivity.newIntent(activity.applicationContext))
                true
            }
            PrefsNames.NOTIFICATION_ACCESS -> {
                showNotificationListenerSettingsIfAvailable()
                true
            }
            else -> false
        }
    }

    private fun setupSummaries() {
        findPreference(Const.PrefsNames.MAX_DURATION).summary = getMaxDurationSummary()
        findPreference(Const.PrefsNames.ABOUT).summary = getAboutSummary()
    }

    private fun getAboutSummary(): CharSequence {
        return resources.getString(R.string.prefs_summary_about, BuildConfig.VERSION_NAME)
    }

    private fun getMaxDurationSummary(): CharSequence {
        val duration = preferenceManager.sharedPreferences
                .getString(PrefsNames.MAX_DURATION, PrefsValues.DELAY_DEFAULT)

        return getString(
                when (duration) {
                    PrefsValues.DELAY_FAST -> R.string.prefs_duration_fast
                    PrefsValues.DELAY_MODERATE -> R.string.prefs_duration_moderate
                    PrefsValues.DELAY_SLOW -> R.string.prefs_duration_slow
                    else -> R.string.empty_string
                }
        )
    }

    @SuppressLint("NewApi")
    private fun showNotificationListenerSettingsIfAvailable() {
        try {
            startActivity(Intent(ActionNames.NOTIFICATION_LISTENER_SETTINGS))
        } catch (e: Exception) {
            val pref = findPreference(PrefsNames.NOTIFICATION_ACCESS)
            pref.isEnabled = false
            pref.summary = resources.getString(R.string.prefs_title_notification_summary_disabled)
            LogUtils.REMOTE_LOG(e)
        }
    }
}