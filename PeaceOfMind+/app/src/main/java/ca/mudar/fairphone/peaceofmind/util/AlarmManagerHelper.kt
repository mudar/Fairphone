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

package ca.mudar.fairphone.peaceofmind.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.support.v4.app.AlarmManagerCompat
import ca.mudar.fairphone.peaceofmind.Const.RequestCodes
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.receiver.AlarmBroadcastReceiver

class AlarmManagerHelper(val context: ContextWrapper) {
    private val TAG = "AlarmManagerHelper"

    fun set() {
        val endTime = UserPrefs(context).getAtPeaceRun().endTime
                ?: return

        toggleWakeupAlarm(endTime)
    }

    fun cancel() {
        toggleWakeupAlarm()
    }

    private fun toggleWakeupAlarm(target: Long? = null) {
        val alarmIntent = AlarmBroadcastReceiver.newIntent(context)
        val pendingIntent = PendingIntent.getBroadcast(context,
                RequestCodes.AT_PEACE_TIMER,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        when (target) {
            null -> alarmManager.cancel(pendingIntent)
            else -> AlarmManagerCompat.setAndAllowWhileIdle(alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    target,
                    pendingIntent)
        }
    }
}
