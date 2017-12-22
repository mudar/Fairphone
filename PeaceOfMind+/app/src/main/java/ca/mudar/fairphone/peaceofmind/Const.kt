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

package ca.mudar.fairphone.peaceofmind

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings


object Const {

    const val APP_PREFS_NAME = "peaceofmind"

    /**
     * SeekArc, seekBar and progressBar
     */
    object SeekArc {
        const val GRANULARITY = 5 // 5-min granularity
        const val SWEEP_ANGLE = 240 // Ref: R.dimen.seekBar_sweepAngle
    }


    object ActionNames {
        const val RINGER_MODE_CHANGED = AudioManager.RINGER_MODE_CHANGED_ACTION
        const val AIRPLANE_MODE_CHANGED = Intent.ACTION_AIRPLANE_MODE_CHANGED
        const val REBOOT = Intent.ACTION_REBOOT
        const val SHUTDOWN = Intent.ACTION_SHUTDOWN
        const val DND_OFF = "com.android.systemui.action.dnd_off"

        @SuppressLint("InlinedApi")
        const val NOTIFICATION_POLICY_ACCESS_SETTINGS = Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
        @SuppressLint("InlinedApi")
        const val NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
    }

    /**
     * Fragment Tags
     */
    object FragmentTags {
        const val HELP = "f_help"
        const val SETTINGS = "settings"
    }

    /**
     * Preferences, ref: strings_common.xml
     */
    object PrefsNames {
        const val MAX_DURATION = "prefs_duration"
        const val HAS_AIRPLANE_MODE = "prefs_has_airplane_mode"
        const val ABOUT = "prefs_about"
        const val NOTIFICATION_ACCESS = "prefs_notification_access"

        const val HAS_ROOT_ACCESS = "prefs_has_root_access"
        const val IS_FIRST_LAUNCH = "prefs_is_first_launch"
        const val IS_AT_PEACE = "prefs_is_at_peace"
        const val DISPLAY_MODE = "prefs_display_mode"
        const val AT_PEACE_MODE = "prefs_at_peace_mode"
        const val PREVIOUS_NOISY_MODE = "prefs_previous_noisy_mode"

        const val STATS_IS_IN_PEACE_OF_MIND = "stats_is_in_peace_of_mind"
        const val STATS_RUN_DURATION = "stats_run_duration"
        const val STATS_RUN_START_TIME = "stats_run_start_time"
        const val STATS_RUN_TARGET_TIME = "stats_run_target_time"
    }

    object PrefsValues {
        const val DELAY_FAST = "3"
        const val DELAY_MODERATE = "6"
        const val DELAY_SLOW = "12"
        const val DELAY_DEFAULT = DELAY_FAST

        @SuppressLint("InlinedApi")
        val AT_PEACE_MODE_DEFAULT = CompatHelper.getDefaultAtPeaceMode()
        @SuppressLint("InlinedApi")
        val NOISY_MODE_DEFAULT = CompatHelper.getDefaultNoisyMode()
    }

    object LocalAssets {
        const val LICENSE = "apache-license-2.0.html"
    }

    const val PLAIN_TEXT_MIME_TYPE = "text/plain"

    const val ASSETS_URI = "file:///android_asset/"


    // Device compatibility
    val SUPPORTS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    val SUPPORTS_LOLLIPOP_MR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
    val SUPPORTS_MARSHMALLOW = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}
