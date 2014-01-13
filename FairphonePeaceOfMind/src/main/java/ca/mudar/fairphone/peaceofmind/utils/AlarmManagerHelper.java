/*
 * Copyright (C) 2013 Fairphone Project
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

package ca.mudar.fairphone.peaceofmind.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.receiver.PeaceOfMindBroadCastReceiver;

public class AlarmManagerHelper {
    private static final String TAG = "AlarmManagerHelper";

    public static void enableAlarm(Context context, long target) {
        toggleWakeupAlarm(context, target);
        toggleWidgetRepeatingAlarm(context, target);
    }

    public static void disableAlarm(Context context, boolean wasInterrupted) {
        if (wasInterrupted) {
            toggleWakeupAlarm(context, -1l);
        }
        toggleWidgetRepeatingAlarm(context, -1l);
    }

    public static void updateAlarm(Context context, long target) {
        toggleWakeupAlarm(context, target);
        toggleWidgetRepeatingAlarm(context, target);
    }

    private static void toggleWakeupAlarm(Context context, long target) {
        final Intent alarmIntent = new Intent(context, PeaceOfMindBroadCastReceiver.class);
        alarmIntent.setAction(Const.PeaceOfMindActions.END_PEACE_OF_MIND);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (target == -1l) {
            alarmManager.cancel(pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    target,
                    pendingIntent);
        }
    }

    private static void toggleWidgetRepeatingAlarm(Context context, long target) {
        final Intent alarmIntent = new Intent(context, PeaceOfMindBroadCastReceiver.class);
        alarmIntent.setAction(Const.PeaceOfMindActions.WIDGET_TIMER_TICK);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (target == -1l) {
            alarmManager.cancel(pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC,
                    TimeHelper.getNextRoundedMinuteTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    pendingIntent);
        }
    }
}
