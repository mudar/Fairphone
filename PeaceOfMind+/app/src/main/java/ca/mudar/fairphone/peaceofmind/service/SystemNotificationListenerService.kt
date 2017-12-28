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

package ca.mudar.fairphone.peaceofmind.service

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.support.annotation.RequiresApi
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.BundleKeys
import ca.mudar.fairphone.peaceofmind.Const.PrefsValues
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.util.CompatHelper

class SystemNotificationListenerService : NotificationListenerService() {
    private val TAG = "SystemNotifListenerService"

    companion object {
        fun newIntent(context: Context, action: String, atPeaceMode: Int? = null): Intent {
            val intent = Intent(context, SystemNotificationListenerService::class.java)
            intent.action = action
            atPeaceMode?.let {
                val extras = Bundle()
                extras.putInt(BundleKeys.MODE, atPeaceMode)
                intent.putExtras(extras)
            }

            return intent
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        setNotificationListener(true)

        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        setNotificationListener(false)

        return super.onUnbind(intent)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        setNotificationListener(true)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        setNotificationListener(false)
    }

    override fun onInterruptionFilterChanged(interruptionFilter: Int) {
        super.onInterruptionFilterChanged(interruptionFilter)

        val userPrefs = UserPrefs(ContextWrapper(application))

        userPrefs.setNotificationListener(true) // Update UserPrefs, just in case

        if (userPrefs.isAtPeace() && userPrefs.getAtPeaceMode() != interruptionFilter) {
            userPrefs.setAtPeace(false)

            startService(AtPeaceForegroundService
                    .newIntent(this, Const.ActionNames.AT_PEACE_SERVICE_WEAK_STOP))
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Const.SUPPORTS_LOLLIPOP) {
            val atPeaceMode = intent.extras?.getInt(BundleKeys.MODE, PrefsValues.AT_PEACE_MODE_DEFAULT)
                    ?: PrefsValues.AT_PEACE_MODE_DEFAULT

            when (intent.action) {
                ActionNames.NOTIFICATION_LISTENER_START -> startPeaceOfMind(atPeaceMode)
                ActionNames.NOTIFICATION_LISTENER_STOP -> endPeaceOfMind()
                ActionNames.NOTIFICATION_LISTENER_UPDATE -> setAtPeaceMode(atPeaceMode)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(notification: StatusBarNotification) {
        if (UserPrefs(ContextWrapper(applicationContext)).isAtPeace()) {
            CompatHelper.cancelStatusBarNotification(this, notification)
        }
    }

    override fun onNotificationRemoved(notification: StatusBarNotification?) {
        // Nothing to do here
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startPeaceOfMind(mode: Int) {
        UserPrefs(ContextWrapper(application)).setPreviousNoisyMode(currentInterruptionFilter)
        requestInterruptionFilter(mode)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun endPeaceOfMind() {
        requestInterruptionFilter(UserPrefs(ContextWrapper(application)).getPreviousNoisyMode())

        startService(AtPeaceForegroundService
                .newIntent(this, Const.ActionNames.AT_PEACE_SERVICE_WEAK_STOP))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAtPeaceMode(mode: Int) {
        requestInterruptionFilter(mode)
    }

    private fun setNotificationListener(enabled: Boolean) {
        UserPrefs(ContextWrapper(application)).setNotificationListener(enabled)
    }
}
