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
import ca.mudar.fairphone.peaceofmind.PeaceOfMindApp
import ca.mudar.fairphone.peaceofmind.bus.AppEvents
import ca.mudar.fairphone.peaceofmind.util.AirplaneModeHelper
import ca.mudar.fairphone.peaceofmind.util.PermissionsManager

@RequiresApi(Build.VERSION_CODES.M)
class NotificationManagerController(context: ContextWrapper) : PeaceOfMindController(context) {
    private val TAG = "NotifMgrController"

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    override fun startPeaceOfMind() {
        if (!isPeaceOfMindOn() && hasPermission()) {
            userPrefs.setPreviousNoisyMode(notificationManager.currentInterruptionFilter)
            userPrefs.setAtPeace(true)
            setNotificationManagerInterruptionFilter(userPrefs.getAtPeaceMode())

            if (userPrefs.isAtPeaceOfflineMode()) {
                AirplaneModeHelper.startAtPeaceOfflineMode(context)
            }
        }
    }

    override fun endPeaceOfMind() {
        if (isPeaceOfMindOn() && hasPermission()) {
            userPrefs.setAtPeace(false)

            revertAtPeaceDndMode()
            revertAtPeaceOfflineMode()
        }
    }

    override fun revertAtPeaceDndMode() {
        setNotificationManagerInterruptionFilter(userPrefs.getPreviousNoisyMode())
    }

    override fun isPeaceOfMindOn(): Boolean {
        return userPrefs.isAtPeace() &&
                (userPrefs.getAtPeaceMode() == notificationManager.currentInterruptionFilter)
    }

    override fun setTotalSilenceMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_NONE, false)
    }

    override fun setAlarmsOnlyMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_ALARMS, false)
    }

    override fun setPriorityOnlyMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_PRIORITY, false)
    }

    override fun setAtPeaceOfflineMode() {
        setAtPeaceMode(NotificationManager.INTERRUPTION_FILTER_NONE, true)
    }

    private fun setAtPeaceMode(mode: Int, offlineMode: Boolean) {
        if (isPeaceOfMindOn() && hasPermission()) {
            setNotificationManagerInterruptionFilter(mode)

            if (userPrefs.hasAirplaneMode()) {
                AirplaneModeHelper.toggleAtPeaceOfflineMode(context, offlineMode)
            }
        }
        userPrefs.setAtPeaceMode(mode, offlineMode)
    }

    private fun hasPermission(): Boolean {
        return if (PermissionsManager.checkNotificationsPolicyAccess(ContextWrapper(context))) {
            true
        } else {
            PeaceOfMindApp.eventBus.post(AppEvents.NotificationListenerPermsRequired())
            false
        }
    }

    private fun setNotificationManagerInterruptionFilter(value: Int) {
        if (notificationManager.currentInterruptionFilter != value) {
            notificationManager.setInterruptionFilter(value)
        }
    }
}
