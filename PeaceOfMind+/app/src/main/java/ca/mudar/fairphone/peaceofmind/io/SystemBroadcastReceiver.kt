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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ca.mudar.fairphone.peaceofmind.Const.IntentNames

class SystemBroadcastReceiver : BroadcastReceiver() {
    private val tag = "AppBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            IntentNames.RINGER_MODE_CHANGED -> onRingerModeChanged()
            IntentNames.AIRPLANE_MODE -> onAirplaneMode()
            IntentNames.ACTION_SHUTDOWN -> onRebootOrShutdown()
            IntentNames.REBOOT -> onRebootOrShutdown()
        }
    }

    private fun onRingerModeChanged() {
        Log.v(tag, "onRingerModeChanged")

    }

    private fun onAirplaneMode() {
        Log.v(tag, "onAirplaneMode")
    }

    private fun onRebootOrShutdown() {
        Log.v(tag, "onRebootOrShutdown")
    }
}