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

package ca.mudar.fairphone.peaceofmind.model

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import ca.mudar.fairphone.peaceofmind.R

data class DndModeButton(@IdRes val id: Int) {
    @StringRes
    val label: Int
    @DrawableRes
    val iconActive: Int
    @DrawableRes
    val iconIdle: Int

    init {
        when (id) {
            R.id.btn_airplane_mode -> {
                label = R.string.airplane_mode
                iconActive = R.drawable.ic_airplane_mode_white
                iconIdle = R.drawable.ic_airplane_mode_white70
            }
            R.id.btn_dnd_total_silence -> {
                label = R.string.dnd_total_silence
                iconActive = R.drawable.ic_dnd_total_silence_white
                iconIdle = R.drawable.ic_dnd_total_silence_white70
            }
            R.id.btn_dnd_alarms_only -> {
                label = R.string.dnd_alarms_only
                iconActive = R.drawable.ic_dnd_alarms_only_white
                iconIdle = R.drawable.ic_dnd_alarms_only_white70
            }
            R.id.btn_dnd_priority_only -> {
                label = R.string.dnd_priority_only
                iconActive = R.drawable.ic_dnd_priority_only_white
                iconIdle = R.drawable.ic_dnd_priority_only_white70
            }
            R.id.btn_ringer_none -> {
                label = R.string.ringer_none
                iconActive = R.drawable.ic_ringer_none_white
                iconIdle = R.drawable.ic_ringer_none_white70
            }
            R.id.btn_ringer_priority -> {
                label = R.string.ringer_priority
                iconActive = R.drawable.ic_ringer_priority_white
                iconIdle = R.drawable.ic_ringer_priority_white70
            }
            else -> {
                label = R.string.empty_string
                iconActive = R.drawable.empty_drawable
                iconIdle = R.drawable.empty_drawable
            }
        }
    }
}
