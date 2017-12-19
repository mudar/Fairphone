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

package ca.mudar.fairphone.peaceofmind.ui.binding

import android.databinding.BindingAdapter
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatTextView
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.util.TextFormatter
import ca.mudar.fairphone.peaceofmind.util.TimeHelper


@BindingAdapter("progressValue", "displayMode", requireAll = false)
fun setTimerLabel(textView: AppCompatTextView,
                  progress: Int?,
                  @DisplayMode mode: String = DisplayMode._DEFAULT) {
    val context = textView.context
    progress?.let {
        val label = when (mode) {
            DisplayMode.END_TIME -> TimeHelper.minutesToEndTimeLabel(context,
                    progress * Const.SeekArc.GRANULARITY)
            else -> TimeHelper.minutesToDurationLabel(context,
                    progress * Const.SeekArc.GRANULARITY)
        }

        textView.text = TextFormatter.getStyledText(context, label, mode)
    }
}

@BindingAdapter("drawableStartRes")
fun setTextViewDrawableResource(textView: AppCompatTextView, @DrawableRes drawableRes: Int?) {
    drawableRes?.let {
        val drawable = VectorDrawableCompat
                .create(textView.resources, drawableRes, textView.context.theme)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}
