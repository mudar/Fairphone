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

package ca.mudar.fairphone.peaceofmind.data

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.PrefsNames
import ca.mudar.fairphone.peaceofmind.Const.PrefsValues
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.model.DisplayMode


class UserPrefs constructor(context: ContextWrapper) {
    private val tag = "UserPrefs"

    private val sharedPrefs: SharedPreferences = context
            .getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE)
    private val prefsEditor: SharedPreferences.Editor = sharedPrefs.edit()

    companion object {
        fun setDefaultPrefs(context: ContextWrapper) {
            PreferenceManager.setDefaultValues(context.applicationContext,
                    Const.APP_PREFS_NAME,
                    Context.MODE_PRIVATE,
                    R.xml.prefs_defaults,
                    false)
        }
    }

    fun registerChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun isFirstLaunch(): Boolean {
        val result = sharedPrefs.getBoolean(PrefsNames.IS_FIRST_LAUNCH, true)

        // Set immediately to false, can be true only once
        prefsEditor.putBoolean(PrefsNames.IS_FIRST_LAUNCH, false)
                .apply()

        return result
    }

    fun getMaxDuration(): Int {
        return sharedPrefs.getString(PrefsNames.MAX_DURATION, PrefsValues.DELAY_DEFAULT)
                .toInt()
    }

//    fun getMaxDurationMillis(): Long {
//        val duration = getMaxDuration()
//        return DateUtils.HOUR_IN_MILLIS * duration
//    }

    fun isAtPeace(): Boolean {
        return sharedPrefs.getBoolean(PrefsNames.IS_AT_PEACE, false)
    }

    fun setAtPeace(enabled: Boolean) {
        prefsEditor.putBoolean(PrefsNames.IS_AT_PEACE, enabled)
                .apply()
    }

    fun hasAirplaneMode(): Boolean {
        return sharedPrefs.getBoolean(PrefsNames.HAS_AIRPLANE_MODE, false)
    }

    fun setAirplaneMode(enabled: Boolean) {
        prefsEditor.putBoolean(PrefsNames.HAS_AIRPLANE_MODE, enabled)
                .apply()
    }

    fun hasRootAccess(): Boolean {
        return sharedPrefs.getBoolean(PrefsNames.HAS_ROOT_ACCESS, false)
    }

    fun setRootAcces(enabled: Boolean) {
        prefsEditor.putBoolean(PrefsNames.HAS_ROOT_ACCESS, enabled)
                .apply()
    }

    @DisplayMode
    fun getDisplayMode(): String {
        return sharedPrefs.getString(PrefsNames.DISPLAY_MODE, DisplayMode._DEFAULT)
    }

//    fun setDisplayMode(@DisplayMode displayMode: String) {
//        prefsEditor.putString(PrefsNames.DISPLAY_MODE, displayMode)
//                .apply()
//    }

    fun toggleDisplayMode() {
        val mode = when (getDisplayMode()) {
            DisplayMode.END_TIME -> DisplayMode.DURATION
            DisplayMode.DURATION -> DisplayMode.END_TIME
            else -> DisplayMode._DEFAULT
        }
        prefsEditor.putString(PrefsNames.DISPLAY_MODE, mode)
                .apply()
    }

    fun getPreviousNoisyMode(): Int {
        return sharedPrefs.getInt(PrefsNames.PREVIOUS_NOISY_MODE, PrefsValues.NOISY_MODE_DEFAULT)
    }

    fun setPreviousNoisyMode(mode: Int) {
        prefsEditor.putInt(PrefsNames.PREVIOUS_NOISY_MODE, mode)
                .apply()
    }

    fun getAtPeaceMode(): Int {
        return sharedPrefs.getInt(PrefsNames.AT_PEACE_MODE, PrefsValues.AT_PEACE_MODE_DEFAULT)
    }

    fun setAtPeaceMode(mode: Int) {
        prefsEditor.putInt(PrefsNames.AT_PEACE_MODE, mode)
                .apply()
    }
}