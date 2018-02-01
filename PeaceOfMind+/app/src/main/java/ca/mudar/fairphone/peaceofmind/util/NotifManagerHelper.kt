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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.R

object NotifManagerHelper {

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotifChannelIfNecessary(context: ContextWrapper) {
        if (Const.SUPPORTS_OREO) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val audioAttrs = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

            val channel = NotificationChannel(Const.NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT)
            channel.setBypassDnd(true)
            channel.enableVibration(false)
            channel.setSound(null, audioAttrs)
            channel.description = context.getString(R.string.notif_channel_description)

            notificationManager.createNotificationChannel(channel)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun updateNotifChannelSettings(context: ContextWrapper) {
        if (Const.SUPPORTS_OREO) {
            val intent = Intent(ActionNames.CHANNEL_NOTIFICATION_SETTINGS)

            val extras = Bundle()
            extras.putString(Settings.EXTRA_CHANNEL_ID, Const.NOTIFICATION_CHANNEL_ID)
            extras.putString(Settings.EXTRA_APP_PACKAGE, context.packageName)

            intent.putExtras(extras)

            context.startActivity(intent)
        }
    }
}
