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

/*
Modifications (MN 2013-12-16):
- Removed empty methods: onEnabled(), onDisabled() and onDeleted()
*/

package ca.mudar.fairphone.peaceofmind.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.R;
import ca.mudar.fairphone.peaceofmind.data.PeaceOfMindPrefs;
import ca.mudar.fairphone.peaceofmind.ui.PeaceOfMindActivity;
import ca.mudar.fairphone.peaceofmind.utils.TimeHelper;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = WidgetProvider.class.getSimpleName();
    private long mMaxTime;
    private PeaceOfMindPrefs mCurrentStats;

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateUI(context, appWidgetManager, appWidgetId);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private void loadCurrentStats(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mCurrentStats = PeaceOfMindPrefs.getStatsFromSharedPreferences(prefs);
        mMaxTime = PeaceOfMindPrefs.getMaxDuration(prefs);
    }

    private void updateUI(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        loadCurrentStats(context);

        // get the widgets
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);

        if (mCurrentStats.mIsOnPeaceOfMind) {
            updateWidgetForPeaceOfMind(context, widget);
        } else {
            updateWidgetForOffPeaceOfMind(context, widget);
        }

        // set the the app link
        Intent intent = new Intent(context, PeaceOfMindActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        widget.setOnClickPendingIntent(R.id.peaceOfMindWidgetLayout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, widget);

    }

    private void updateWidgetForOffPeaceOfMind(Context context, RemoteViews widget) {
        // disable off peace of mind text
        widget.setViewVisibility(R.id.onGroup, View.GONE);
        widget.setViewVisibility(R.id.offGroup, View.VISIBLE);

        int maxTime = (int) mMaxTime / 1000;

        // set progress bar
        widget.setProgressBar(R.id.progressBar, maxTime, 0, false);

        widget.setViewPadding(R.id.timerTexts, 0, 0, 0, 0);
        widget.setViewPadding(R.id.peaceOfMindText, 0, 0, 0, 0);

        widget.setProgressBar(R.id.secondaryProgressBar, maxTime, 0, false);
    }

    private void setTimeText(Context context, long time, int hoursId, RemoteViews widgets) {
        int hours = (int) (time / Const.HOUR);
        int timeInMinutes = (int) (time - hours * Const.HOUR);
        int minutes;

        if (hours == 0) {
            minutes = timeInMinutes - Const.MINUTE > 0 ? timeInMinutes / Const.MINUTE : 1;
        } else {
            minutes = timeInMinutes / Const.MINUTE;
        }

        String timeStr = String.format("%d%s%02d", hours, context.getResources().getString(R.string.hour_separator), minutes);
        if (hoursId == R.id.timeText) {
            if (hours == 0) {
                widgets.setTextViewText(R.id.toText, context.getResources().getString(R.string.to_m));
            } else {
                widgets.setTextViewText(R.id.toText, context.getResources().getString(R.string.to_h));
            }
        }
        widgets.setTextViewText(hoursId, timeStr);

    }

    private void updateWidgetForPeaceOfMind(Context context, RemoteViews widget) {
        // disable off peace of mind text
        widget.setViewVisibility(R.id.onGroup, View.VISIBLE);
        widget.setViewVisibility(R.id.offGroup, View.GONE);

        // set the time
        final long currentTime = TimeHelper.getRoundedCurrentTimeMillis();
        final long pastTime = currentTime - mCurrentStats.mCurrentRun.mStartTime;
        long timeUntilTarget = mCurrentStats.mCurrentRun.mDuration - pastTime;
        setTimeText(context, timeUntilTarget , R.id.timeText, widget);
        setTimeText(context, mCurrentStats.mCurrentRun.mDuration, R.id.totalTimeText, widget);

        // set progress bar
        int maxTime = (int) mMaxTime / 1000;
        int progress = (int) pastTime / 1000;
        widget.setProgressBar(R.id.progressBar, maxTime, progress, false);

        int secondaryProgress = (int) mCurrentStats.mCurrentRun.mDuration / 1000;
        widget.setProgressBar(R.id.secondaryProgressBar, maxTime, secondaryProgress, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
     * android.appwidget.AppWidgetManager, int[])
     * 
     * OnUpdate ==============================================================
     * context The Context in which this receiver is running. appWidgetManager A
     * AppWidgetManager object you can call updateAppWidget(ComponentName,
     * RemoteViews) on. appWidgetIds The appWidgetIds for which an update is
     * needed. Note that this may be all of the AppWidget instances for this
     * provider, or just a subset of them.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Called in response to the ACTION_APPWIDGET_UPDATE broadcast when this
        // AppWidget provider
        // is being asked to provide RemoteViews for a set of AppWidgets.
        // Override this method to implement your own AppWidget functionality.

        // iterate through every instance of this widget
        // remember that it can have more than one widget of the same type.
        for (int appWidgetId : appWidgetIds) {
            updateUI(context, appWidgetManager, appWidgetId);
        }

    }
}
