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

package ca.mudar.fairphone.peaceofmind.utils;

import android.content.res.Resources;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Locale;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.R;

public class TimeHelper {
    private static final String TAG = "TimeHelper";

    public static String[] generateStringTimeFromMillis(long timePast, boolean reset, Resources res) {
        int hours = 0;
        int minutes = 0;
        if (!reset) {
            hours = (int) (timePast / Const.HOUR);
            int timeInMinutes = (int) (timePast - hours * Const.HOUR);

            if (hours == 0) {
                minutes = timeInMinutes - Const.MINUTE > 0 ? timeInMinutes / Const.MINUTE : 1;
            } else {
                minutes = timeInMinutes / Const.MINUTE;
            }
        }

        final String timeStr = String.format(Locale.US, "%d%s%02d", hours, res.getString(R.string.hour_separator), minutes);
        final String timeToStr = res.getString(hours == 0 ? R.string.to_m : R.string.to_h);

        return new String[]{timeStr, timeToStr};
    }

    public static long roundToInterval(long time) {

        int hours = (int) (time / Const.HOUR);
        int minutes = (int) ((time - hours * Const.HOUR) / Const.MINUTE);
        long newTime = 0;

        int index = minutes % 10;
        switch (index) {
            case 1:
            case 6:
                newTime -= Const.MINUTE;
                break;
            case 2:
            case 7:
                newTime -= 2 * Const.MINUTE;
                break;
            case 3:
            case 8:
                newTime += 2 * Const.MINUTE;
                break;
            case 4:
            case 9:
                newTime += Const.MINUTE;
                break;
        }

        return (hours * Const.HOUR) + (minutes * Const.MINUTE) + newTime;
    }

    public static int getCurrentProgressY(long timePast, long duration, int height, long maxTime) {
        if (duration > 0) {
            float timePercentage = (((float) timePast / (float) maxTime));
            return (int) (0.8f * height * timePercentage + (height * Const.INITIAL_PERCENTAGE));
        } else {
            return (int) (height * Const.INITIAL_PERCENTAGE);
        }
    }

    public static long getRoundedCurrentTimeMillis() {
        final Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);

        return now.getTimeInMillis();
    }

    public static long getNextRoundedMinuteTimeMillis() {
        return DateUtils.MINUTE_IN_MILLIS + getRoundedCurrentTimeMillis();
    }
}
