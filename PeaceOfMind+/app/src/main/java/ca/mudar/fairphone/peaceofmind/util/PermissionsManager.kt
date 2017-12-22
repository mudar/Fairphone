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
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import ca.mudar.fairphone.peaceofmind.Const


object PermissionsManager {

    @TargetApi(Build.VERSION_CODES.M)
    fun requestNotificationsPolicyAccess(activity: Activity) {
        if (Const.SUPPORTS_MARSHMALLOW) {
            if (!checkNotificationsPolicyAccess(activity)) {
                val intent = Intent(Const.ActionNames.NOTIFICATION_POLICY_ACCESS_SETTINGS)
                activity.startActivity(intent)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkNotificationsPolicyAccess(context: ContextWrapper): Boolean {
        if (Const.SUPPORTS_MARSHMALLOW) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Check if the notification policy access has been granted for the app.
            return notificationManager.isNotificationPolicyAccessGranted
        }

        return false
    }
}
