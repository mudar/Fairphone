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
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.support.v4.app.AlarmManagerCompat
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.dnd.AudioManagerController
import ca.mudar.fairphone.peaceofmind.dnd.NotificationListenerController
import ca.mudar.fairphone.peaceofmind.dnd.NotificationManagerController
import ca.mudar.fairphone.peaceofmind.dnd.PeaceOfMindController
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService

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

    /**
     * For API 21+, this is handled elsewhere
     * @see [ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService.onInterruptionFilterChanged]
     */
    fun onRingerModeChanged(context: ContextWrapper) {
        val peaceOfMindController = getPeaceOfMindController(context)
        when {
            Const.SUPPORTS_MARSHMALLOW -> {
                if (UserPrefs(context).hasNotificationListener()) {
                    // Nothing to do here, handled by onInterruptionFilterChanged()
                } else if (!peaceOfMindController.isPeaceOfMindOn()) {
                    context.startService(AtPeaceForegroundService
                            .newIntent(context, Const.ActionNames.AT_PEACE_REVERT_OFFLINE_MODE))
                }
            }
            Const.SUPPORTS_LOLLIPOP -> {
                // Nothing to do here, handled by onInterruptionFilterChanged()
                return
            }
            else -> {
                if (!peaceOfMindController.isPeaceOfMindOn()) {
                    context.startService(AtPeaceForegroundService
                            .newIntent(context, Const.ActionNames.AT_PEACE_REVERT_OFFLINE_MODE))
                }
            }
        }
    }

    fun isAtPeaceOfflineMode(context: ContextWrapper): Boolean {
        return when {
            Const.SUPPORTS_LOLLIPOP -> UserPrefs(context).isAtPeaceOfflineMode()
            else -> UserPrefs(context).hasAirplaneMode()
        }
    }

    @SuppressLint("NewApi")
    fun setAlarm(context: ContextWrapper, target: Long, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Const.SUPPORTS_MARSHMALLOW &&
                !PermissionsManager.checkBatteryOptimizationWhitelist(context)) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(target, pendingIntent),
                    pendingIntent)
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    target,
                    pendingIntent)
        }
    }

    @SuppressLint("NewApi")
    fun showRequiredPermissionIfNecessary(activity: Activity) {
        when {
            Const.SUPPORTS_MARSHMALLOW -> PermissionsManager
                    .showNotificationsPolicyAccessSettingsIfNecessary(activity)
            Const.SUPPORTS_LOLLIPOP -> {
                // This was a hidden action until API 22, but should work for API 21
                if (!UserPrefs(activity).hasNotificationListener()) {
                    PermissionsManager.showNotificationListenerSettings(activity)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun requestRequiredPermission(activity: Activity) {
        when {
            Const.SUPPORTS_MARSHMALLOW -> PermissionsManager
                    .showNotificationsPolicyAccessSettings(activity)
            Const.SUPPORTS_LOLLIPOP -> {
                // This was a hidden action until API 22, but should work for API 21
                PermissionsManager.showNotificationListenerSettings(activity)
            }
        }
    }

    @SuppressLint("NewApi")
    fun checkRequiredPermission(context: ContextWrapper): Boolean {
        return when {
            Const.SUPPORTS_MARSHMALLOW -> PermissionsManager.checkNotificationsPolicyAccess(context)
            Const.SUPPORTS_LOLLIPOP -> UserPrefs(context).hasNotificationListener()
            else -> true
        }
    }

    /**
     * Returns INTERRUPTION_FILTER_CHANGED or RINGER_MODE_CHANGED
     */
    @SuppressLint("NewApi")
    fun getRingerModeChangedActionName(): String {
        return when (Const.SUPPORTS_MARSHMALLOW) {
            true -> ActionNames.INTERRUPTION_FILTER_CHANGED
            false -> ActionNames.RINGER_MODE_CHANGED
        }
    }
}
