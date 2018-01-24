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

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.RequestCodes
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.ui.activity.MainActivity
import ca.mudar.fairphone.peaceofmind.util.*


class AtPeaceForegroundService : IntentService("AtPeaceForegroundService") {
    val TAG = "AtPeaceForegroundService"

    companion object {
        fun newIntent(context: Context, action: String): Intent {
            val intent = Intent(context, AtPeaceForegroundService::class.java)
            intent.action = action

            return intent
        }
    }

//    override fun startForegroundService(service: Intent?): ComponentName {
//        return super.startForegroundService(service)
//    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ActionNames.AT_PEACE_SERVICE_START -> startAtPeace()
            ActionNames.AT_PEACE_SERVICE_END -> endAtPeace()
            ActionNames.AT_PEACE_ALARM_MANAGER_STOP -> showAlarmOrEndAtPeace()
            ActionNames.AT_PEACE_SERVICE_WEAK_STOP -> weakStopAtPeace()
            ActionNames.AT_PEACE_REVERT_DND_MODE -> revertAtPeaceRingerMode()
            ActionNames.AT_PEACE_REVERT_OFFLINE_MODE -> revertAtPeaceAirplaneMode()
        }
    }

    private fun startAtPeace() {
        LogUtils.LOGV(TAG, "startAtPeace")

        showNotification()
        AlarmManagerHelper(ContextWrapper(this)).set()
        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).startPeaceOfMind()
        PackageManagerHelper.setSystemReceiverState(this, true)
    }

    private fun endAtPeace() {
        LogUtils.LOGV(TAG, "endAtPeace")

        weakStopAtPeace()
        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).endPeaceOfMind()
    }

    private fun revertAtPeaceRingerMode() {
        LogUtils.LOGV(TAG, "revertAtPeaceDndMode")

        weakStopAtPeace()
        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).revertAtPeaceDndMode()
        UserPrefs(ContextWrapper(this)).setAtPeace(false)
    }

    private fun revertAtPeaceAirplaneMode() {
        LogUtils.LOGV(TAG, "revertAtPeaceOfflineMode")

        weakStopAtPeace()
        CompatHelper.getPeaceOfMindController(ContextWrapper(this)).revertAtPeaceOfflineMode()
        UserPrefs(ContextWrapper(this)).setAtPeace(false)
    }

    private fun weakStopAtPeace() {
        LogUtils.LOGV(TAG, "weakStopAtPeace")

        cancelNotification()
        AlarmManagerHelper(ContextWrapper(this)).cancel()
        PackageManagerHelper.setSystemReceiverState(this, false)
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

        notificationManager.notify(RequestCodes.AT_PEACE_SERVICE, buildNotification().build())
    }

    private fun buildNotification(): NotificationCompat.Builder {
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
            else -> getString(R.string.notif_text,
                    TimeHelper.getEndTimeLabel(this, endTime))
        }

        return NotificationCompat.Builder(this, Const.NOTIFICATION_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(ContextCompat.getColor(this, R.color.notification_color))
                .setSmallIcon(R.drawable.ic_notify)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.notif_title))
                .setContentText(contentText)
                .setSound(null)
                .setContentIntent(contentPendingIntent)
                .addAction(stopAction)
    }

    private fun cancelNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(RequestCodes.AT_PEACE_SERVICE)
    }
}