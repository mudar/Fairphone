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

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.util.LogUtils

@SuppressLint("OverrideAbstract")
class SystemNotificationListenerService : NotificationListenerService() {
    private val TAG = "SystemNotifListenerService"

    @SuppressLint("InlinedApi")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        LogUtils.LOGV(TAG, "onStartCommand: ")
        if (Const.SUPPORTS_LOLLIPOP) {
            requestInterruptionFilter(NotificationListenerService.INTERRUPTION_FILTER_NONE)
        }

        return result
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.LOGV(TAG, "onCreate")
    }

    override fun onDestroy() {
        LogUtils.LOGV(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onNotificationPosted(notification: StatusBarNotification) {
        LogUtils.LOGV(TAG, "onNotificationPosted : ")
        if (UserPrefs(ContextWrapper(applicationContext)).isAtPeace()) {
            CompatHelper.cancelStatusBarNotification(this, notification)
        }
    }

    override fun onNotificationRemoved(notification: StatusBarNotification?) {
        // Nothing to do here
    }
}
