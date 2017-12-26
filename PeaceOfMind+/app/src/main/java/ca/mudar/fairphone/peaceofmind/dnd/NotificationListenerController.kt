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

import android.content.ContextWrapper
import android.os.Build
import android.service.notification.NotificationListenerService
import android.support.annotation.RequiresApi
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NotificationListenerController(private val context: ContextWrapper) : PeaceOfMindController {
    private val TAG = "NotifListenerController"

    private var userPrefs = UserPrefs(context)

    override fun startPeaceOfMind() {
        if (!isPeaceOfMindOn() && hasPermission()) {
            userPrefs.setAtPeace(true)
            context.startService(SystemNotificationListenerService.newIntent(context,
                    ActionNames.NOTIFICATION_LISTENER_START,
                    userPrefs.getAtPeaceMode()))
        }
    }

    override fun endPeaceOfMind() {
        if (isPeaceOfMindOn() && hasPermission()) {
            userPrefs.setAtPeace(false)
            context.startService(SystemNotificationListenerService.newIntent(context,
                    ActionNames.NOTIFICATION_LISTENER_STOP))
        }
    }

    /**
     * For Lollipop, this is handled elsewhere
     * @see [ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService.onInterruptionFilterChanged]
     */
    override fun forceEndPeaceOfMind() {
        // Nothing to do here, relies on onInterruptionFilterChanged()
    }

    /**
     * For Lollipop, this is handled elsewhere
     * @see [ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService.onInterruptionFilterChanged]
     */
    override fun isPeaceOfMindOn(): Boolean {
        // For Lollipop, no need to check currentPeaceMode, relies on onInterruptionFilterChanged()
        return userPrefs.isAtPeace()
    }

    override fun setSilentRingerMode() {
        setAtPeaceMode(NotificationListenerService.INTERRUPTION_FILTER_NONE)
    }

    override fun setPriorityRingerMode() {
        setAtPeaceMode(NotificationListenerService.INTERRUPTION_FILTER_PRIORITY)
    }

    override fun setTotalSilenceMode() {
        // Nothing to do here
    }

    override fun setAlarmsOnlyMode() {
        // Nothing to do here
    }

    override fun setPriorityOnlyMode() {
        // Nothing to do here
    }

    private fun setAtPeaceMode(mode: Int) {
        if (isPeaceOfMindOn()) {
            context.startService(SystemNotificationListenerService.newIntent(context,
                    ActionNames.NOTIFICATION_LISTENER_UPDATE,
                    mode))
        }
        userPrefs.setAtPeaceMode(mode)
    }

    private fun hasPermission(): Boolean {
        return userPrefs.hasNotificationListener()
    }
}
