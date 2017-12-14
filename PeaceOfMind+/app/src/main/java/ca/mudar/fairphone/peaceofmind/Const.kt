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

import android.os.Build


object Const {

    const val APP_PREFS_NAME = "peaceofmind"


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

        const val IS_ACCESS_GIVEN = "prefs_is_access_given"
        const val IS_FIRST_RUN = "prefs_is_first_run"
        const val PREVIOUS_RINGER_MODE = "prefs_previous_ringer_mode"
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
    }

    object LocalAssets {
        const val LICENSE = "apache-license-2.0.html"
    }

    const val PLAIN_TEXT_MIME_TYPE = "text/plain"

    const val ASSETS_URI = "file:///android_asset/"

    var SUPPORTS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

}
