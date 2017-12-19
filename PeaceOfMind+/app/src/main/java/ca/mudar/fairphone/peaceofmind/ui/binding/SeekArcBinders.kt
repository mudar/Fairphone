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
import com.triggertrap.seekarc.SeekArc

@BindingAdapter("seekArcMax", "seekArcProgress", requireAll = true)
fun setSeekBarValues(seekArc: SeekArc, max: Int?, progress: Int?) {
    max?.let {
        val maxUpdated: Boolean = (seekArc.max != max)
        if (maxUpdated) {
            seekArc.max = max
        }

        progress?.let {
            if (seekArc.progress != progress || maxUpdated) {
                seekArc.progress = progress
            }
        }
    }
}

@BindingAdapter("seekArcSweepAngle", "seekArcProgress", requireAll = true)
fun setProgressBarValues(seekArc: SeekArc, sweepAngle: Int?, progress: Int?) {
    sweepAngle?.let {
        if (seekArc.sweepAngle != sweepAngle) {
            seekArc.sweepAngle = sweepAngle
            seekArc.invalidate() // Needed to update layout
        }

        progress?.let {
            if (seekArc.progress != progress) {
                seekArc.progress = progress
            }
        }
    }
}