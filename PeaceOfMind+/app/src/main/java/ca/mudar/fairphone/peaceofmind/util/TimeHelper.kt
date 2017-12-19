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
import java.util.*

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

    fun minutesToDurationLabel(context: Context, minutes: Int): String {
        val hours = String.format(NON_LEADING_ZERO_FORMAT, minutes / HOUR_IN_MINUTES)
        val paddedMinutes = String.format(LEADING_ZERO_FORMAT, minutes % HOUR_IN_MINUTES)

        return context.resources.getString(R.string.duration_hours_minutes,
                hours, paddedMinutes)
    }

    fun minutesToEndTimeLabel(context: Context, minutes: Int): String {
        val currentTime = GregorianCalendar().time
        val endTime = currentTime.time.plus(minutes * DateUtils.MINUTE_IN_MILLIS)

        return DateFormat.getTimeFormat(context).format(endTime)
    }

}