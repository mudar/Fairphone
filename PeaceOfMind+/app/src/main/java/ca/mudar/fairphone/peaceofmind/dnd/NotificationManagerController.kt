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

package ca.mudar.fairphone.peaceofmind.dnd

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.annotation.RequiresApi
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.util.LogUtils
import ca.mudar.fairphone.peaceofmind.util.PermissionsManager

@RequiresApi(Build.VERSION_CODES.M)
class NotificationManagerController(override val context: ContextWrapper) : PeaceOfMindController {
    private val TAG = "NotifMgrController"

    private var userPrefs = UserPrefs(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    override fun startPeaceOfMind() {
        LogUtils.LOGV(TAG, "startPeaceOfMind")
        if (!isPeaceOfMindOn() && hasPermission()) {
            userPrefs.setPreviousNoisyMode(notificationManager.currentInterruptionFilter)
            userPrefs.setAtPeace(true)
            notificationManager.setInterruptionFilter(UserPrefs(context).getAtPeaceMode())
        }
    }

    override fun endPeaceOfMind() {
        LogUtils.LOGV(TAG, "endPeaceOfMind")
        if (isPeaceOfMindOn() && hasPermission()) {
            val previousNoisyMode = userPrefs.getPreviousNoisyMode()
            userPrefs.setAtPeace(false)
            notificationManager.setInterruptionFilter(previousNoisyMode)
        }
    }

    // TODO("clear timer here")
    override fun forceEndPeaceOfMind() {
        userPrefs.setAtPeace(false)
    }

    override fun isPeaceOfMindOn(): Boolean {
        return userPrefs.isAtPeace() &&
                (userPrefs.getAtPeaceMode() == notificationManager.currentInterruptionFilter)
    }

    override fun setTotalSilenceMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    override fun setAlarmsOnlyMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_ALARMS)
    }

    override fun setPriorityOnlyMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    }

    override fun setSilentRingerMode() {
        // Nothing to do here
    }

    override fun setPriorityRingerMode() {
        // Nothing to do here
    }

    private fun setAtPeaceMode(mode: Int) {
        if (isPeaceOfMindOn() && hasPermission()) {
            notificationManager.setInterruptionFilter(mode)
        }
        userPrefs.setAtPeaceMode(mode)
    }

    // TODO("Send EventBus to request permission")
    private fun hasPermission(): Boolean {
        return if (PermissionsManager.checkNotificationsPolicyAccess(ContextWrapper(context))) {
            true
        } else {
            LogUtils.LOGE(TAG, "TODO: Send EventBus to request permission")
            false
        }
    }
}