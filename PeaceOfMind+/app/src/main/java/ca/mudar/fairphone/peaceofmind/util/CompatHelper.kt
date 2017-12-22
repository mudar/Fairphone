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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.ContextWrapper
import android.media.AudioManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.io.AudioManagerController
import ca.mudar.fairphone.peaceofmind.io.NotificationListenerController
import ca.mudar.fairphone.peaceofmind.io.NotificationManagerController
import ca.mudar.fairphone.peaceofmind.io.PeaceOfMindController

object CompatHelper {

    @SuppressLint("InlinedApi")
    fun getDefaultAtPeaceMode(): Int {
        return when {
            Const.SUPPORTS_MARSHMALLOW -> NotificationManager.INTERRUPTION_FILTER_NONE
            Const.SUPPORTS_LOLLIPOP -> NotificationListenerService.INTERRUPTION_FILTER_NONE
            else -> AudioManager.RINGER_MODE_SILENT
        }
    }

    @SuppressLint("InlinedApi")
    fun getDefaultNoisyMode(): Int {
        return when {
            Const.SUPPORTS_MARSHMALLOW -> NotificationManager.INTERRUPTION_FILTER_ALL
            Const.SUPPORTS_LOLLIPOP -> NotificationListenerService.INTERRUPTION_FILTER_ALL
            else -> AudioManager.RINGER_MODE_NORMAL
        }
    }

    @SuppressLint("NewApi")
    fun getPeaceOfMindController(contextWrapper: ContextWrapper): PeaceOfMindController {
        return when {
            Const.SUPPORTS_MARSHMALLOW -> NotificationManagerController(contextWrapper)
            Const.SUPPORTS_LOLLIPOP -> NotificationListenerController(contextWrapper)
            else -> AudioManagerController(contextWrapper)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun cancelStatusBarNotification(notificationListenerService: NotificationListenerService,
                                    notification: StatusBarNotification) {
        when {
            Const.SUPPORTS_LOLLIPOP -> notificationListenerService
                    .cancelNotification(notification.key)
            else -> notificationListenerService
                    .cancelNotification(notification.packageName, notification.tag, notification.id)
        }
    }
}