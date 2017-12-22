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

package ca.mudar.fairphone.peaceofmind.io

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.util.LogUtils


class SystemBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "SystemBroadcastReceiver"
    lateinit var peaceOfMindController: PeaceOfMindController

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            peaceOfMindController = when {
                Const.SUPPORTS_MARSHMALLOW -> NotificationManagerController(ContextWrapper(context))
                else -> AudioManagerController(ContextWrapper(context))
            }
            when (intent?.action) {
                ActionNames.DND_OFF,
                ActionNames.RINGER_MODE_CHANGED -> onRingerModeChanged(intent)
                ActionNames.AIRPLANE_MODE_CHANGED -> onAirplaneMode()
                ActionNames.REBOOT,
                ActionNames.SHUTDOWN -> onRebootOrShutdown()
                else -> LogUtils.LOGE(TAG, "onReceive, action = " + intent?.action)
            }
        }

        logIntentExtras(intent)
    }

    @SuppressLint("InlinedApi")
    private fun onRingerModeChanged(intent: Intent?) {
        LogUtils.LOGV(TAG, "onRingerModeChanged")
        if (!peaceOfMindController.isPeaceOfMindOn()) {
            peaceOfMindController.forceEndPeaceOfMind()
        }
    }

    private fun onAirplaneMode() {
        LogUtils.LOGV(TAG, "onAirplaneMode")
    }

    private fun onRebootOrShutdown() {
        LogUtils.LOGV(TAG, "onRebootOrShutdown")
        peaceOfMindController.endPeaceOfMind()
    }

    private fun logIntentExtras(intent: Intent?) {
        val bundle = intent?.extras
        bundle?.let {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                LogUtils.LOGV(TAG, String.format("%s = %s", key, value.toString()))
            }
        }
    }
}