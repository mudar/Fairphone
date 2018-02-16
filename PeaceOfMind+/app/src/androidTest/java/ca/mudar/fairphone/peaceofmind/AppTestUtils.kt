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

import android.content.Context
import android.content.ContextWrapper
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService
import ca.mudar.fairphone.peaceofmind.util.TimeHelper
import java.util.Date

object AppTestUtils {
    private val D_01_30_00_000 = 5400000L

    fun resetPrefs(context: Context) {
        context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE).edit()
                .remove(Const.PrefsNames.MAX_DURATION)
                .remove(Const.PrefsNames.HAS_AIRPLANE_MODE)
                .remove(Const.PrefsNames.HAS_END_NOTIFICATION)
                .remove(Const.PrefsNames.NOTIFICATION_VIBRATE)
                .remove(Const.PrefsNames.NOTIFICATION_RINGTONE)
                .remove(Const.PrefsNames.NOTIFICATION_CHANNEL_SETTINGS)
                .remove(Const.PrefsNames.NOTIFICATION_LISTENER_PERMS)
                .remove(Const.PrefsNames.DND_PERMS)
                .remove(Const.PrefsNames.BATTERY_OPTIMIZATION_PERMS)
                .remove(Const.PrefsNames.CATEGORY_NOTIFICATIONS)
                .remove(Const.PrefsNames.HAS_SPLASH)
                .remove(Const.PrefsNames.HAS_USAGE_HINT)
                .remove(Const.PrefsNames.IS_ROOT_AVAILABLE)
                .remove(Const.PrefsNames.IS_AT_PEACE)
                .remove(Const.PrefsNames.DISPLAY_MODE)
                .remove(Const.PrefsNames.AT_PEACE_MODE)
                .remove(Const.PrefsNames.AT_PEACE_OFFLINE_MODE)
                .remove(Const.PrefsNames.PREVIOUS_NOISY_MODE)
                .remove(Const.PrefsNames.PREVIOUS_AIRPLANE_MODE)
                .remove(Const.PrefsNames.HAS_NOTIFICATION_LISTENER)
                .remove(Const.PrefsNames.AT_PEACE_DURATION)
                .remove(Const.PrefsNames.AT_PEACE_END_TIME)
                .commit()

        UserPrefs.setDefaultPrefs(ContextWrapper(context))
    }

    fun setAtPeaceRun(prefs: UserPrefs, duration: Long = D_01_30_00_000) {
        prefs.setAtPeaceRun(AtPeaceRun(duration,
                TimeHelper.getEndTimeForDuration(duration, Date().time)
        ))
    }

    fun startAtPeaceService(context: Context, action: String) {
        context.startService(AtPeaceForegroundService
                .newIntent(context, action))
        Thread.sleep(2000)
    }
}