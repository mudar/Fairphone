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
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.model.DisplayMode

object TextFormatter {
    private const val durationScaleFactor = 0.5f
    private const val endTimeScaleFactor = 0.25f
    private val numeralsCharArray = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    fun getStyledText(context: Context, text: String, @DisplayMode mode: String): Spannable {

        val span = SpannableString(text)

        if (mode == DisplayMode.DURATION) {
            // Reduce size of `h` separator char
            val hIndex = text.length - 3 // Index of `h` in `1h20`
            span.setSpan(RelativeSizeSpan(durationScaleFactor),
                    hIndex,
                    hIndex + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (mode == DisplayMode.END_TIME && !DateFormat.is24HourFormat(context)) {
            // Reduce size of AM/PM and change color

            val secondaryTextColor = ContextCompat.getColor(context, R.color.text_secondary)
            val lastDigitIndex = text.lastIndexOfAny(numeralsCharArray)

            if (lastDigitIndex != -1) {
                span.setSpan(ForegroundColorSpan(secondaryTextColor),
                        lastDigitIndex + 1,
                        text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                span.setSpan(RelativeSizeSpan(endTimeScaleFactor),
                        lastDigitIndex + 1,
                        text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return span
    }
}