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
import android.content.IntentFilter
import android.os.Bundle
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.BundleKeys
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.dnd.PeaceOfMindController
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.util.LogUtils


class SystemBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "SystemBroadcastReceiver"
    lateinit var peaceOfMindController: PeaceOfMindController

    companion object {
        fun registerReceiver(context: ContextWrapper, receiver: BroadcastReceiver) {
            // RINGER_MODE_CHANGED or INTERRUPTION_FILTER_CHANGED
            val ringerModeChanged = CompatHelper.getRingerModeChangedActionName()

            try {
                context.registerReceiver(receiver, IntentFilter(ActionNames.DND_OFF))
                context.registerReceiver(receiver, IntentFilter(ringerModeChanged))
                context.registerReceiver(receiver, IntentFilter(ActionNames.AIRPLANE_MODE_CHANGED))
                context.registerReceiver(receiver, IntentFilter(ActionNames.REBOOT))
                context.registerReceiver(receiver, IntentFilter(ActionNames.SHUTDOWN))
            } catch (e: Exception) {
                LogUtils.REMOTE_LOG(e)
            }
        }

        fun unregisterReceiver(context: ContextWrapper, receiver: BroadcastReceiver) {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val contextWrapper = ContextWrapper(context)
            peaceOfMindController = CompatHelper.getPeaceOfMindController(contextWrapper)

            // RINGER_MODE_CHANGED or INTERRUPTION_FILTER_CHANGED
            val ringerModeChanged = CompatHelper.getRingerModeChangedActionName()

            when (intent?.action) {
                ActionNames.DND_OFF,
                ringerModeChanged -> onRingerModeChanged(contextWrapper)
                ActionNames.AIRPLANE_MODE_CHANGED -> onAirplaneModeChanged(contextWrapper, intent.extras)
                ActionNames.REBOOT,
                ActionNames.SHUTDOWN -> onRebootOrShutdown()
                else -> LogUtils.LOGV(TAG, "onReceive, action = " + intent?.action)
            }
        }
    }

    private fun onRingerModeChanged(context: ContextWrapper) {
        CompatHelper.onRingerModeChanged(context)
    }

    private fun onAirplaneModeChanged(context: ContextWrapper, bundle: Bundle?) {
        val isOwnIntent = bundle?.containsKey(BundleKeys.AT_PEACE_TOGGLE) ?: false
        if (!isOwnIntent && UserPrefs(context).hasAirplaneMode()) {
            context.startService(AtPeaceForegroundService
                    .newIntent(context, Const.ActionNames.AT_PEACE_REVERT_DND_MODE))
        }
    }

    private fun onRebootOrShutdown() {
        peaceOfMindController.endPeaceOfMind()
    }

//    private fun logIntentExtras(intent: Intent?) {
//        val bundle = intent?.extras
//        bundle?.let {
//            for (key in bundle.keySet()) {
//                val value = bundle.get(key)
//                LogUtils.LOGV(TAG, String.format("%s = %s", key, value.toString()))
//            }
//        }
//    }
}
