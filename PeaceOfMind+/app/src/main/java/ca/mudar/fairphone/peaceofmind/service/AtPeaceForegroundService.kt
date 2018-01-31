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

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.RequestCodes
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.receiver.SystemBroadcastReceiver
import ca.mudar.fairphone.peaceofmind.ui.activity.MainActivity
import ca.mudar.fairphone.peaceofmind.util.AlarmManagerHelper
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.util.LogUtils
import ca.mudar.fairphone.peaceofmind.util.NotifManagerHelper
import ca.mudar.fairphone.peaceofmind.util.TimeHelper


class AtPeaceForegroundService : Service() {
    val TAG = "AtPeaceForegroundService"

    private val receiver = SystemBroadcastReceiver()

    companion object {
        fun newIntent(context: Context, action: String): Intent {
            val intent = Intent(context, AtPeaceForegroundService::class.java)
            intent.action = action

            return intent
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ActionNames.AT_PEACE_SERVICE_START -> startAtPeace()
            ActionNames.AT_PEACE_SERVICE_END -> endAtPeace()
            ActionNames.AT_PEACE_ALARM_MANAGER_STOP -> showAlarmOrEndAtPeace()
            ActionNames.AT_PEACE_SERVICE_WEAK_STOP -> weakStopAtPeace()
            ActionNames.AT_PEACE_REVERT_DND_MODE -> revertAtPeaceRingerMode()
            ActionNames.AT_PEACE_REVERT_OFFLINE_MODE -> revertAtPeaceAirplaneMode()
        }

        return START_STICKY
    }

    private fun startAtPeace() {
        LogUtils.LOGV(TAG, "startAtPeace")

        showNotification()
        AlarmManagerHelper(ContextWrapper(this)).set()
        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).startPeaceOfMind()
        SystemBroadcastReceiver.registerReceiver(this, receiver)
    }

    private fun endAtPeace() {
        LogUtils.LOGV(TAG, "endAtPeace")

        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).endPeaceOfMind()
        weakStopAtPeace()
    }

    private fun revertAtPeaceRingerMode() {
        LogUtils.LOGV(TAG, "revertAtPeaceRingerMode")

        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).revertAtPeaceDndMode()
        UserPrefs(ContextWrapper(this)).setAtPeace(false)
        weakStopAtPeace()
    }

    private fun revertAtPeaceAirplaneMode() {
        LogUtils.LOGV(TAG, "revertAtPeaceAirplaneMode")

        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).revertAtPeaceOfflineMode()
        UserPrefs(ContextWrapper(this)).setAtPeace(false)
        weakStopAtPeace()
    }

    private fun weakStopAtPeace() {
        LogUtils.LOGV(TAG, "weakStopAtPeace")

        AlarmManagerHelper(ContextWrapper(this)).cancel()
        SystemBroadcastReceiver.unregisterReceiver(this, receiver)
        showEndOrCancelNotification()
    }

    private fun showAlarmOrEndAtPeace() {
        val userPrefs = UserPrefs(ContextWrapper(this))
        val endTime = userPrefs.getAtPeaceRun().endTime

        endTime?.let {
            if (System.currentTimeMillis() < endTime) {
                // Before endTime, this is a click status bar alarm icon
                startActivity(MainActivity.newIntent(this))
                userPrefs.setDisplayMode(DisplayMode.END_TIME)
            } else {
                // After endTime, this is the alarm's pendingIntent to end atPeace
                endAtPeace()
            }
        }
    }

    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotifManagerHelper.createNotifChannelIfNecessary(ContextWrapper(this))

        notificationManager.notify(RequestCodes.AT_PEACE_SERVICE, buildStartNotification().build())
    }

    private fun buildStartNotification(): NotificationCompat.Builder {
        val stopPendingIntent = PendingIntent.getService(this,
                RequestCodes.AT_PEACE_SERVICE,
                AtPeaceForegroundService.newIntent(this, ActionNames.AT_PEACE_SERVICE_END),
                PendingIntent.FLAG_UPDATE_CURRENT)

        val contentPendingIntent = PendingIntent.getActivity(this,
                RequestCodes.MAIN_ACTIVITY,
                MainActivity.newIntent(this),
                PendingIntent.FLAG_UPDATE_CURRENT)

        val stopAction = NotificationCompat.Action(R.drawable.ic_stop_white,
                getString(R.string.notif_action_stop),
                stopPendingIntent)

        val endTime = UserPrefs(ContextWrapper(this)).getAtPeaceRun().endTime
        val contentText = when (endTime) {
            null -> null
            else -> getString(R.string.notif_start_text,
                    TimeHelper.getEndTimeLabel(this, endTime))
        }

        return NotificationCompat.Builder(this, Const.NOTIFICATION_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(ContextCompat.getColor(this, R.color.notification_color))
                .setSmallIcon(R.drawable.ic_notify)
                .setOngoing(true)
                .setShowWhen(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.notif_start_title))
                .setContentText(contentText)
                .setContentIntent(contentPendingIntent)
                .setSound(null)
                .addAction(stopAction)
    }

    private fun buildEndNotification(): NotificationCompat.Builder {
        val contentPendingIntent = PendingIntent.getActivity(this,
                RequestCodes.MAIN_ACTIVITY,
                MainActivity.newIntent(this),
                PendingIntent.FLAG_UPDATE_CURRENT)

        val contentText = getString(R.string.notif_end_text,
                TimeHelper.getEndTimeLabel(this, System.currentTimeMillis()))

        val builder = NotificationCompat.Builder(this, Const.NOTIFICATION_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(NotificationCompat.COLOR_DEFAULT)
                .setSmallIcon(R.drawable.ic_notify)
                .setOngoing(false)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.notif_end_title))
                .setContentText(contentText)
                .setContentIntent(contentPendingIntent)

        // Vibration and sound
        val userPrefs = UserPrefs(ContextWrapper(this))
        if (userPrefs.hasNotificationVibration()) {
            builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        }
        builder.setSound(userPrefs.getNotificationRingtonePath())

        return builder
    }

    private fun showEndOrCancelNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (UserPrefs(ContextWrapper(this)).hasEndNotification()) {
            true -> notificationManager.notify(RequestCodes.AT_PEACE_SERVICE, buildEndNotification().build())
            false -> notificationManager.cancel(RequestCodes.AT_PEACE_SERVICE)
        }
    }
}
