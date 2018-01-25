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

import android.app.NotificationManager
import android.databinding.BindingAdapter
import android.service.notification.NotificationListenerService
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatButton
import android.view.View
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R

@BindingAdapter("currentAtPeaceMode", "currentOfflineMode", requireAll = true)
fun setDndCurrentMode(button: AppCompatButton,
                      atPeaceMode: Int?,
                      isAtPeaceOfflineMode: Boolean? = false) {
    val context = button.context
            ?: return

    @DrawableRes var icon: Int? = null
    @StringRes var label: Int? = null

    when {
        Const.SUPPORTS_MARSHMALLOW -> when {
            isAtPeaceOfflineMode == true -> {
                label = R.string.airplane_mode
                icon = R.drawable.ic_airplane_mode_white
            }
            atPeaceMode == NotificationManager.INTERRUPTION_FILTER_NONE -> {
                label = R.string.dnd_total_silence
                icon = R.drawable.ic_dnd_total_silence_white
            }
            atPeaceMode == NotificationManager.INTERRUPTION_FILTER_ALARMS -> {
                label = R.string.dnd_alarms_only
                icon = R.drawable.ic_dnd_alarms_only_white
            }
            atPeaceMode == NotificationManager.INTERRUPTION_FILTER_PRIORITY -> {
                label = R.string.dnd_priority_only
                icon = R.drawable.ic_dnd_priority_only_white
            }
        }
        Const.SUPPORTS_LOLLIPOP -> when {
            isAtPeaceOfflineMode == true -> {
                label = R.string.airplane_mode
                icon = R.drawable.ic_airplane_mode_white
            }
            atPeaceMode == NotificationListenerService.INTERRUPTION_FILTER_NONE -> {
                label = R.string.ringer_none
                icon = R.drawable.ic_ringer_none_white
            }
            atPeaceMode == NotificationListenerService.INTERRUPTION_FILTER_PRIORITY -> {
                label = R.string.ringer_priority
                icon = R.drawable.ic_ringer_priority_white
            }
        }
        else -> {
            button.visibility = View.GONE
            return
        }
    }

    button.text = when {
        (label != null) -> context.getText(label)
        else -> null
    }

    val drawable = when {
        (icon != null) -> VectorDrawableCompat.create(context.resources, icon, context.theme)
        else -> null
    }
    button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}