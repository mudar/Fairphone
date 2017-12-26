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

package ca.mudar.fairphone.peaceofmind.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

object TimeHelper {
    private val HOUR_IN_MINUTES = 60
    private val NON_LEADING_ZERO_FORMAT = "%d"
    private val LEADING_ZERO_FORMAT = "%02d"

    fun hoursToSeekArcValue(hours: String): Int {
        return try {
            hoursToSeekArcValue(hours.toInt())
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun hoursToSeekArcValue(hours: Int): Int {
        return hours * HOUR_IN_MINUTES / Const.SeekArc.GRANULARITY
    }

    fun getDurationForProgress(progress: Int,
                               @DisplayMode displayMode: String = DisplayMode._DEFAULT,
                               startTime: Long?): Long {
        fun getCeilDurationForStartTime(duration: Long, startTime: Long): Long {
            val roundStartTime = Const.Timer.END_TIME_ROUND *
                    ceil(startTime.toDouble() / Const.Timer.END_TIME_ROUND).toLong()

            return duration + (roundStartTime - startTime)
        }

        if (progress <= 0) {
            return 0
        }

        val duration = progressToMillis(progress)
        return when (displayMode) {
            DisplayMode.END_TIME -> getCeilDurationForStartTime(duration, getTimeWithoutSeconds(startTime))
            else -> duration
        }
    }

    fun getEndTimeForDuration(duration: Long?, startTime: Long?): Long {
        return getTimeWithoutSeconds(startTime) + (duration ?: 0)
    }

    fun getEndTimeLabel(context: Context, endTime: Long?): String {
        val millis = endTime ?: getTimeWithoutSeconds(Date().time)

        return DateFormat.getTimeFormat(context).format(millis)
    }

    fun getDurationLabel(context: Context, duration: Long?): String {
        val minutes = (duration ?: 0) / DateUtils.MINUTE_IN_MILLIS
        val hours = String.format(NON_LEADING_ZERO_FORMAT, minutes / HOUR_IN_MINUTES)
        val paddedMinutes = String.format(LEADING_ZERO_FORMAT, minutes % HOUR_IN_MINUTES)

        return context.resources.getString(R.string.duration_hours_minutes,
                hours, paddedMinutes)
    }

    private fun progressToMillis(progress: Int): Long {
        return when {
            (progress <= 0) -> 0L
            else -> progress * Const.SeekArc.GRANULARITY * DateUtils.MINUTE_IN_MILLIS
        }
    }

    private fun millisToProgress(millis: Long): Int {
        return when {
            (millis <= 0) -> 0
            else -> (millis / (Const.SeekArc.GRANULARITY * DateUtils.MINUTE_IN_MILLIS)).toInt()
        }
    }

    private fun getTimeWithoutSeconds(time: Long?): Long {
        val calendar = GregorianCalendar()
        time?.let {
            calendar.timeInMillis = time
        }

        // Set millis and seconds to zero
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }

    fun getProgressForAtPeaceRun(atPeaceRun: AtPeaceRun, @DisplayMode displayMode: String = DisplayMode._DEFAULT): Int {
        fun getFloorDuration(duration: Long): Long {
            return Const.Timer.END_TIME_ROUND *
                    floor(duration.toDouble() / Const.Timer.END_TIME_ROUND).toLong()
        }

        if (atPeaceRun.duration == null || atPeaceRun.duration <= 0) {
            return 0
        }

        val duration = when (displayMode) {
            DisplayMode.END_TIME -> getFloorDuration(atPeaceRun.duration)
            else -> atPeaceRun.duration
        }
        return millisToProgress(duration)
    }
}
