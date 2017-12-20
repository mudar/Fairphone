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
import android.media.AudioManager
import android.os.Build


object Const {

    const val APP_PREFS_NAME = "peaceofmind"

    /**
     * SeekArc, seekBar and progressBar
     */
    object SeekArc {
        const val GRANULARITY = 5 // 5-min granularity
        const val SWEEP_ANGLE = 240 // Ref: R.dimen.seekBar_sweepAngle
    }

    object IntentNames {
        const val RINGER_MODE_CHANGED = "android.media.RINGER_MODE_CHANGED"
        const val AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE"
        const val REBOOT = "android.intent.action.REBOOT"
        const val ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN"
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
        val AT_PEACE_MODE_DEFAULT = when (SUPPORTS_NOTIFICATION_POLICY) {
            true -> NotificationManager.INTERRUPTION_FILTER_NONE
            false -> AudioManager.RINGER_MODE_SILENT
        }
        @SuppressLint("InlinedApi")
        val NOISY_MODE_DEFAULT = when (SUPPORTS_NOTIFICATION_POLICY) {
            true -> NotificationManager.INTERRUPTION_FILTER_ALL
            false -> AudioManager.RINGER_MODE_NORMAL
        }
    }

    object LocalAssets {
        const val LICENSE = "apache-license-2.0.html"
    }

    const val PLAIN_TEXT_MIME_TYPE = "text/plain"

    const val ASSETS_URI = "file:///android_asset/"

    // Device compatibility
    val SUPPORTS_VECTOR_DRAWABLES = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    val SUPPORTS_NOTIFICATION_POLICY = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

}
