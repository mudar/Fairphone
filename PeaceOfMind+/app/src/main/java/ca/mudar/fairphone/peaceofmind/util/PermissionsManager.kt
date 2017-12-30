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

import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.support.annotation.RequiresApi
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames


object PermissionsManager {

    /**
     * Check if the notification policy access has been granted for the app.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkNotificationsPolicyAccess(context: ContextWrapper): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return notificationManager.isNotificationPolicyAccessGranted
    }

    /**
     * Show Notifications policy access settings, only if not granted for the app.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun showNotificationsPolicyAccessSettingsIfNecessary(context: ContextWrapper) {
        if (Const.SUPPORTS_MARSHMALLOW) {
            if (!checkNotificationsPolicyAccess(context)) {
                showNotificationsPolicyAccessSettings(context)
            }
        }
    }

    /**
     * Show Notifications policy access settings, regardless of granted status
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun showNotificationsPolicyAccessSettings(context: ContextWrapper) {
        if (Const.SUPPORTS_MARSHMALLOW) {
            context.startActivity(Intent(ActionNames.NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }

    /**
     * Check if the app is whitelisted on the battery optimization list
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBatteryOptimizationWhitelist(context: ContextWrapper): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Show battery optimization settings, only if the app is not whitelisted
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun showBatteryOptimizationSettingsIfNecessary(context: ContextWrapper) {
        if (Const.SUPPORTS_MARSHMALLOW) {

            if (!checkBatteryOptimizationWhitelist(context)) {
                showBatteryOptimizationSettings(context)
            }
        }
    }

    /**
     * Show battery optimization settings, regardless of whitelist status
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun showBatteryOptimizationSettings(context: ContextWrapper) {
        if (Const.SUPPORTS_MARSHMALLOW) {
            context.startActivity(Intent(ActionNames.IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }

    /**
     * Show notification access settings. Status cannot be checked.
     * This was a hidden action until API 22, but should work on our minSdkVersion 19.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun showNotificationListenerSettings(context: ContextWrapper) {
        context.startActivity(Intent(ActionNames.NOTIFICATION_LISTENER_SETTINGS))
    }
}
