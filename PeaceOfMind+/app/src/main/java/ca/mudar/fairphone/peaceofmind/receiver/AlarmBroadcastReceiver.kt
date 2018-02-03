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

package ca.mudar.fairphone.peaceofmind.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.support.v4.content.ContextCompat
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService
import ca.mudar.fairphone.peaceofmind.ui.activity.MainActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "AlarmBroadcastReceiver"

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = ActionNames.AT_PEACE_ALARM_MANAGER_STOP

            return intent
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == ActionNames.AT_PEACE_ALARM_MANAGER_STOP) {
            // This is false when user clicks the alarm icon in the status bar
            val isPendingAlarm = intent.hasExtra(Intent.EXTRA_ALARM_COUNT)

            when (isPendingAlarm) {
                true -> ContextCompat.startForegroundService(context,
                        AtPeaceForegroundService.newIntent(context, ActionNames.AT_PEACE_SERVICE_END))
                false -> {
                    context.startActivity(MainActivity.newIntent(context))
                    UserPrefs(ContextWrapper(context)).setDisplayMode(DisplayMode.END_TIME)
                }
            }
        }
    }
}