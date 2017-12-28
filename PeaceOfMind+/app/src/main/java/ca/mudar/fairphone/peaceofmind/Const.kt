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

import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.text.format.DateUtils
import ca.mudar.fairphone.peaceofmind.util.CompatHelper


object Const {

    const val APP_PREFS_NAME = "peaceofmind"

    /**
     * SeekArc, seekBar and progressBar
     */
    object SeekArc {
        const val GRANULARITY = 5 // 5-min granularity
        const val SWEEP_ANGLE = 240 // Ref: R.integer.seekBar_sweepAngle
        const val PROGRESS_BAR_MAX = 100 // Ref: R.integer.progressBar_max
    }

    object BundleKeys {
        const val MODE = "mode"
//        const val PAST_TIME = "past_time"
//        const val DURATION = "duration"
    }

    object Timer {
        const val END_TIME_ROUND = 5 * DateUtils.MINUTE_IN_MILLIS // 5-min granularity
    }

    object ActionNames {
//        const val STARTED = "ca.mudar.fairphone.peaceofmind.STARTED"
//        const val UPDATED = "ca.mudar.fairphone.peaceofmind.UPDATED"
//        const val ENDED = "ca.mudar.fairphone.peaceofmind.ENDED"
//        const val TICK = "ca.mudar.fairphone.peaceofmind.TICK"

        const val RINGER_MODE_CHANGED = AudioManager.RINGER_MODE_CHANGED_ACTION
        const val AIRPLANE_MODE_CHANGED = Intent.ACTION_AIRPLANE_MODE_CHANGED
        const val REBOOT = Intent.ACTION_REBOOT
        const val SHUTDOWN = Intent.ACTION_SHUTDOWN
        const val DND_OFF = "com.android.systemui.action.dnd_off"
        const val NOTIFICATION_LISTENER_START = "ca.mudar.fairphone.peaceofmind.NOTIFICATION_LISTENER_START"
        const val NOTIFICATION_LISTENER_STOP = "ca.mudar.fairphone.peaceofmind.NOTIFICATION_LISTENER_STOP"
        const val NOTIFICATION_LISTENER_UPDATE = "ca.mudar.fairphone.peaceofmind.NOTIFICATION_LISTENER_UPDATE"

        const val AT_PEACE_SERVICE_START = "ca.mudar.fairphone.peaceofmind.AT_PEACE_SERVICE_START"
        const val AT_PEACE_SERVICE_END = "ca.mudar.fairphone.peaceofmind.AT_PEACE_SERVICE_END"
        const val AT_PEACE_SERVICE_FORCE_END = "ca.mudar.fairphone.peaceofmind.AT_PEACE_SERVICE_FORCE_END"
        const val AT_PEACE_SERVICE_WEAK_STOP = "ca.mudar.fairphone.peaceofmind.AT_PEACE_SERVICE_WEAK_STOP"

        @RequiresApi(Build.VERSION_CODES.M)
        const val NOTIFICATION_POLICY_ACCESS_SETTINGS = Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
        const val NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
    }

    object RequestCodes {
        const val AT_PEACE_TIMER = 110
        const val AT_PEACE_SERVICE = 120
        const val MAIN_ACTIVITY = 130
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
        const val HAS_NOTIFICATION_LISTENER = "prefs_has_notification_listener"
        const val AT_PEACE_DURATION = "prefs_at_peace_duration"
        const val AT_PEACE_END_TIME = "prefs_at_peace_end_time"

        const val STATS_RUN_DURATION = "stats_run_duration"
        const val STATS_RUN_START_TIME = "stats_run_start_time"
        const val STATS_RUN_TARGET_TIME = "stats_run_target_time"
    }

    object PrefsValues {
        const val DELAY_FAST = "3"
        const val DELAY_MODERATE = "6"
        const val DELAY_SLOW = "12"
        const val DELAY_DEFAULT = DELAY_FAST

        val AT_PEACE_MODE_DEFAULT = CompatHelper.getDefaultAtPeaceMode()
        val NOISY_MODE_DEFAULT = CompatHelper.getDefaultNoisyMode()

        val NULLABLE_LONG = -1L
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
    val SUPPORTS_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}
