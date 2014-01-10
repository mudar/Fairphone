/*
 * Copyright (C) 2013 Fairphone Project
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

/*
Modifications (MN 2013-12-16):
- Added PM_STATS_IS_SILENT_MODE_ONLY 
- Added mIsSilentModeOnly with getter/setter
*/

package ca.mudar.fairphone.peaceofmind.data;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateUtils;

import ca.mudar.fairphone.peaceofmind.Const;

public class PeaceOfMindPrefs {

    private static final String PM_STATS_IS_IN_PEACE_OF_MIND = "PM_STATS_IS_IN_PEACE_OF_MIND";
    private static final String PM_STATS_RUN_DURATION = "PM_STATS_RUN_DURATION";
    private static final String PM_STATS_RUN_START_TIME = "PM_STATS_RUN_START_TIME";
    private static final String PM_STATS_RUN_TARGET_TIME = "PM_STATS_RUN_TARGET_TIME";
    private static final String PM_PREFS_MAX_DURATION = "PM_PREFS_MAX_DURATION";
    private static final String PM_PREFS_HAS_AIRPLANE_MODE = "PM_PREFS_HAS_AIRPLANE_MODE";
    private static final String PM_PREFS_IS_ACCESS_GIVEN = "PM_PREFS_IS_ACCESS_GIVEN";
    private static final String PM_PREFS_IS_FIRST_RUN = "PM_PREFS_IS_FIRST_RUN";
    public boolean mIsOnPeaceOfMind;
    public PeaceOfMindRun mCurrentRun;

    public static PeaceOfMindPrefs getStatsFromSharedPreferences(SharedPreferences preferences) {
        PeaceOfMindPrefs stats = new PeaceOfMindPrefs();

        stats.mIsOnPeaceOfMind = preferences.getBoolean(PM_STATS_IS_IN_PEACE_OF_MIND, false);

        if (stats.mIsOnPeaceOfMind) {
            stats.mCurrentRun = new PeaceOfMindRun();
            stats.mCurrentRun.mDuration = preferences.getLong(PM_STATS_RUN_DURATION, 0);
            stats.mCurrentRun.mStartTime = preferences.getLong(PM_STATS_RUN_START_TIME, 0);
            stats.mCurrentRun.mTargetTime = preferences.getLong(PM_STATS_RUN_TARGET_TIME, 0);
        }

        return stats;
    }

    public static void saveToSharedPreferences(PeaceOfMindPrefs stats, SharedPreferences preferences) {
        Editor editor = preferences.edit();

        editor.putBoolean(PM_STATS_IS_IN_PEACE_OF_MIND, stats.mIsOnPeaceOfMind);

        if (stats.mIsOnPeaceOfMind) {
            editor.putLong(PM_STATS_RUN_DURATION, stats.mCurrentRun.mDuration);
            editor.putLong(PM_STATS_RUN_START_TIME, stats.mCurrentRun.mStartTime);
            editor.putLong(PM_STATS_RUN_TARGET_TIME, stats.mCurrentRun.mTargetTime);
        }

        editor.commit();
    }

    public static boolean hasAirplaneMode(SharedPreferences preferences) {
        return preferences.getBoolean(PM_PREFS_HAS_AIRPLANE_MODE, true);
    }

    public static void setAirplaneMode(boolean isSilentModeOnly, SharedPreferences preferences) {
        Editor editor = preferences.edit();
        editor.putBoolean(PM_PREFS_HAS_AIRPLANE_MODE, isSilentModeOnly);
        editor.commit();
    }

    public static long getMaxDuration(SharedPreferences preferences) {
        final String sDuration = preferences.getString(PM_PREFS_MAX_DURATION, String.valueOf(Const.MAX_TIME_DEFAULT));

        return DateUtils.HOUR_IN_MILLIS * Long.valueOf(sDuration);
    }

    public static boolean isAccessGiven(SharedPreferences preferences) {
        return preferences.getBoolean(PM_PREFS_IS_ACCESS_GIVEN, false);
    }

    public static void setAccessGiven(boolean isAccessGiven, SharedPreferences preferences) {
        Editor editor = preferences.edit();
        editor.putBoolean(PM_PREFS_IS_ACCESS_GIVEN, isAccessGiven);
        editor.commit();
    }

    public static boolean isFirstRun(SharedPreferences preferences) {
        return preferences.getBoolean(PM_PREFS_IS_FIRST_RUN, true);
    }

    public static void setHasRunOnce(SharedPreferences preferences) {
        Editor editor = preferences.edit();
        editor.putBoolean(PM_PREFS_IS_FIRST_RUN, false);
        editor.commit();
    }

    public interface PrefsNames {
        public static final String MAX_DURATION = PM_PREFS_MAX_DURATION;
        public static final String HAS_AIRPLANE_MODE = PM_PREFS_HAS_AIRPLANE_MODE;
        public static final String IS_ACCESS_GIVEN = PM_PREFS_IS_ACCESS_GIVEN;
    }

    public interface PrefsValues {
        final String DELAY_FAST = "3";
        final String DELAY_MODERATE = "6";
        final String DELAY_SLOW = "12";
    }
}
