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

package ca.mudar.fairphone.peaceofmind.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.PrefsNames
import ca.mudar.fairphone.peaceofmind.Const.PrefsValues
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.util.TimeHelper
import kotlin.math.floor

class AtPeaceViewModel : ViewModel() {
    private val TAG = "AtPeaceViewModel"

    private var userPrefs: UserPrefs? = null
    val title = ObservableInt(R.string.app_name)
    val subtitle = ObservableInt(R.string.subtitle_duration)
    val subtitleIcon = ObservableInt(R.drawable.ic_timelapse_white)
    val maxDuration = ObservableInt()
    val isAtPeace = ObservableBoolean(false)
    val seekBarProgress = ObservableInt(0)
    val progressBarSweepAngle = ObservableInt(0)
    val progressBarProgress = ObservableInt(0)
    val displayMode = ObservableField<@DisplayMode String>()
    val duration = ObservableField<Long?>()
    val endTime = ObservableField<Long?>()
    val hasAirplaneMode = ObservableBoolean(false)

    private val prefsListener = SharedPreferences
            .OnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    PrefsNames.IS_AT_PEACE -> updateAtPeace()
                    PrefsNames.DISPLAY_MODE -> updateDisplayMode()
                    PrefsNames.MAX_DURATION -> updateMaxDuration()
                    PrefsNames.AT_PEACE_END_TIME -> updateAtPeaceTime()
                    PrefsNames.HAS_AIRPLANE_MODE -> updateAirplaneModeVisibility()
                }
            }

    init {
        maxDuration.set(TimeHelper.hoursToSeekArcValue(PrefsValues.DELAY_DEFAULT))
    }


    /**
     * Must be called by activity to allow ViewModel to load initial data
     */
    fun loadData(prefs: UserPrefs) {
        userPrefs = prefs
        userPrefs?.registerChangeListener(prefsListener)

        updateAtPeace()
        updateDisplayMode()
        updateMaxDuration()
        updateAirplaneModeVisibility()

        setSeekBarProgress(TimeHelper
                .getProgressForAtPeaceRun(prefs.getAtPeaceRun(), prefs.getDisplayMode()),
                prefs.getAtPeaceRun().startTime,
                false)
        updateAtPeaceTime()
        updateProgressBarProgress()
    }

    /**
     * Allow the activity's listener to update seekBar value
     */
    fun setSeekBarProgress(progress: Int, startTime: Long?, fromUser: Boolean) {
        if (progress != seekBarProgress.get()) {
            seekBarProgress.set(progress)
            updateProgressBarMax(progress)

            if (fromUser) {
                duration.set(TimeHelper.getDurationForProgress(progress, displayMode.get(), startTime))
                endTime.set(TimeHelper.getEndTimeForDuration(duration.get(), startTime))
            }
        }
    }

    /**
     * Toggle display mode onClick()
     */
    fun toggleDisplayMode() {
        userPrefs?.toggleDisplayMode()
    }

    /**
     * Remove sharedPrefs change listener when necessary
     */
    override fun onCleared() {
        super.onCleared()

        userPrefs?.unregisterChangeListener(prefsListener)
    }


    private fun updateMaxDuration() {
        val duration = userPrefs?.getMaxDuration()
                ?: return

        maxDuration.set(TimeHelper.hoursToSeekArcValue(duration))
        updateProgressBarMax(seekBarProgress.get())
    }

    private fun updateAtPeaceTime() {
        val prefs = userPrefs
                ?: return

        val atPeaceRun = prefs.getAtPeaceRun()
        duration.set(atPeaceRun.duration)
        endTime.set(atPeaceRun.endTime)
    }

    private fun updateAtPeace() {
        val atPeace = userPrefs?.isAtPeace()
                ?: return

        when (atPeace) {
            true -> {
                title.set(R.string.title_at_peace_on)
                isAtPeace.set(true)
            }
            false -> {
                title.set(R.string.title_at_peace_off)
                seekBarProgress.set(0)
                progressBarSweepAngle.set(0)
                progressBarProgress.set(0)
                isAtPeace.set(false)
            }
        }
    }

    private fun updateDisplayMode() {
        val mode = userPrefs?.getDisplayMode()
                ?: return

        displayMode.set(mode)
        when (mode) {
            DisplayMode.END_TIME -> {
                subtitle.set(R.string.subtitle_end_time)
                subtitleIcon.set(R.drawable.ic_alarm_white)
            }
            DisplayMode.DURATION -> {
                subtitle.set(R.string.subtitle_duration)
                subtitleIcon.set(R.drawable.ic_timelapse_white)
            }
        }
    }

    private fun updateProgressBarMax(progress: Int) {
        val percentage: Float = progress.toFloat() / maxDuration.get()
        progressBarSweepAngle.set((Const.SeekArc.SWEEP_ANGLE * percentage).toInt())
    }

    private fun updateAirplaneModeVisibility() {
        val enabled = userPrefs?.hasAirplaneMode()
                ?: return

        hasAirplaneMode.set(enabled)
    }

    fun updateProgressBarProgress() {
        val atPeaceRun = userPrefs?.getAtPeaceRun()
                ?: return

        val elapsedPercentage = TimeHelper.getAtPeaceElapsedPercentage(atPeaceRun)
        progressBarProgress.set(floor(elapsedPercentage * 100).toInt())
    }
}
