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
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.RingtonePreference
import android.text.TextUtils
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.PrefsNames
import ca.mudar.fairphone.peaceofmind.Const.PrefsValues
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.util.LogUtils
import ca.mudar.fairphone.peaceofmind.util.PermissionsManager
import ca.mudar.fairphone.peaceofmind.util.SuperuserHelper


class SettingsFragment : PreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private var durationPref: Preference? = null
    private var hasAirplaneModePref: CheckBoxPreference? = null
    private var notificationRingtonePref: RingtonePreference? = null
    private var notificationListenerPermsPref: CheckBoxPreference? = null
    private var dndPermsPref: CheckBoxPreference? = null
    private var batteryOptimizationPermsPref: CheckBoxPreference? = null

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
        hasAirplaneModePref = findPreference(PrefsNames.HAS_AIRPLANE_MODE) as CheckBoxPreference?
        notificationRingtonePref = findPreference(PrefsNames.NOTIFICATION_RINGTONE) as RingtonePreference?
        notificationListenerPermsPref = findPreference(PrefsNames.NOTIFICATION_LISTENER_PERMS) as CheckBoxPreference?
        dndPermsPref = findPreference(PrefsNames.DND_PERMS) as CheckBoxPreference?
        batteryOptimizationPermsPref = findPreference(PrefsNames.BATTERY_OPTIMIZATION_PERMS) as CheckBoxPreference?

        removeUnavailablePreferences()

        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        setupSummaries()
        setupPermissionsStatus()
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
        when (key) {
            PrefsNames.MAX_DURATION -> {
                durationPref?.summary = getMaxDurationSummary()
                // Trigger onActivityResult, to clip duration if necessary
                activity.setResult(Activity.RESULT_OK)
            }
            PrefsNames.HAS_AIRPLANE_MODE -> checkRootForAirplaneMode()
            PrefsNames.NOTIFICATION_RINGTONE -> notificationRingtonePref?.summary = getRingtoneSummary()
        }
    }

    /**
     * Implements Preference.OnPreferenceChangeListener
     * This allows the checkbox state to change only if permission changed. UI updates onResume()
     */
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            PrefsNames.NOTIFICATION_LISTENER_PERMS -> showNotificationListenerSettingsIfAvailable()
            PrefsNames.DND_PERMS -> PermissionsManager
                    .showNotificationsPolicyAccessSettings(activity)
            PrefsNames.BATTERY_OPTIMIZATION_PERMS -> PermissionsManager
                    .showBatteryOptimizationSettings(activity)
        }

        return false // to stop UI changes
    }

    private fun setupListeners() {
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        notificationListenerPermsPref?.onPreferenceChangeListener = this
        dndPermsPref?.onPreferenceChangeListener = this
        batteryOptimizationPermsPref?.onPreferenceChangeListener = this
    }

    private fun setupSummaries() {
        durationPref?.summary = getMaxDurationSummary()
        notificationRingtonePref?.summary = getRingtoneSummary()
    }

    @SuppressLint("NewApi")
    private fun setupPermissionsStatus() {
        notificationListenerPermsPref?.isChecked = preferenceManager
                .sharedPreferences.getBoolean(PrefsNames.HAS_NOTIFICATION_LISTENER, false)

        if (Const.SUPPORTS_MARSHMALLOW) {
            dndPermsPref?.isChecked = PermissionsManager.checkNotificationsPolicyAccess(ContextWrapper(context))
            batteryOptimizationPermsPref?.isChecked = PermissionsManager.checkBatteryOptimizationWhitelist(ContextWrapper(context))
        }
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

    private fun getRingtoneSummary(): CharSequence {
        val path = preferenceManager.sharedPreferences
                .getString(PrefsNames.NOTIFICATION_RINGTONE, PrefsValues.RINGTONE_SILENT)

        if (!TextUtils.isEmpty(path)) {
            val context = activity.applicationContext

            val ringtone = RingtoneManager.getRingtone(context, Uri.parse(path))
            if (ringtone == null) {
                // revert to silent ringtone
                preferenceManager.sharedPreferences
                        .edit()
                        .putString(PrefsNames.NOTIFICATION_RINGTONE, PrefsValues.RINGTONE_SILENT)
                        .apply()
            } else {
                return ringtone.getTitle(context)
            }
        }

        return resources.getString(R.string.prefs_summary_notification_ringtone_silent)
    }


    @SuppressLint("NewApi")
    private fun showNotificationListenerSettingsIfAvailable() {
        try {
            // This was a hidden action until API 22, but should work on our minSdkVersion 19.
            PermissionsManager.showNotificationListenerSettings(activity)
        } catch (e: Exception) {
            LogUtils.REMOTE_LOG(e)
            val pref = notificationListenerPermsPref
                    ?: return
            pref.isEnabled = false
            pref.summary = resources.getString(R.string.prefs_summary_notification_listener_disabled)
        }
    }

    private fun removeUnavailablePreferences() {
        removeRootCategoryIfNotAvailable()
        removeVibrationIfNotSupported()
    }

    private fun removeRootCategoryIfNotAvailable() {
        val isRootAvailable = preferenceManager
                .sharedPreferences.getBoolean(PrefsNames.IS_ROOT_AVAILABLE, false)
        if (!isRootAvailable) {
            preferenceScreen.removePreference(hasAirplaneModePref)

            SuperuserHelper.checkRootAvailability()
        }
    }

    private fun removeVibrationIfNotSupported() {
        val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (!v.hasVibrator()) {
            val notificationVibratePref = (findPreference(PrefsNames.NOTIFICATION_VIBRATE) as? CheckBoxPreference)

            notificationVibratePref?.let {
                notificationVibratePref.isChecked = false
                val parentCategory = findPreference(PrefsNames.CATEGORY_NOTIFICATIONS) as? PreferenceCategory
                parentCategory?.removePreference(notificationVibratePref)
            }
        }
    }

    private fun checkRootForAirplaneMode() {
        val hasAirplaneMode = preferenceManager
                .sharedPreferences.getBoolean(PrefsNames.HAS_AIRPLANE_MODE, false)

        hasAirplaneModePref?.isChecked = hasAirplaneMode
        if (hasAirplaneMode) {
            SuperuserHelper.isAccessGiven()
        }
    }
}
