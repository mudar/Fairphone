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

package ca.mudar.fairphone.peaceofmind.dashclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.R;
import ca.mudar.fairphone.peaceofmind.data.PeaceOfMindPrefs;
import ca.mudar.fairphone.peaceofmind.ui.PeaceOfMindActivity;
import ca.mudar.fairphone.peaceofmind.utils.TimeHelper;

public class AtPeaceDashClockExtension extends DashClockExtension {
    private static final String TAG = "AtPeaceDashClockExtension";
    private static final String TEXT_SPACER = " ";
    private DashclockTimerTickReceiver dashclockTimerTickReceiver;

    public static void updateDashClock(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.PeaceOfMindActions.DASHCLOCK_TIMER_TICK));
    }

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);

        registerForPeaceOfMindBroadCasts();
    }

    @Override
    protected void onUpdateData(int reason) {
        final PeaceOfMindPrefs currentStats = getCurrentStats();

        if (currentStats == null || !currentStats.mIsOnPeaceOfMind) {
            clearUpdateExtensionData();
        } else {
            publishUpdateExtensionData(currentStats);
        }
    }

    /**
     * Unregister the listener before ending the extension
     */
    @Override
    public void onDestroy() {
        unRegisterForPeaceOfMindBroadCasts();

        super.onDestroy();
    }

    /**
     * Clear DashClock if not running
     */
    private void clearUpdateExtensionData() {
        publishUpdate(null);
    }

    /**
     * publishUpdate. Display information about time remaining/spent
     */
    private void publishUpdateExtensionData(PeaceOfMindPrefs currentStats) {
        final Resources res = getApplicationContext().getResources();

        final long pastTime = TimeHelper.getRoundedCurrentTimeMillis() - currentStats.mCurrentRun.mStartTime;
        final long timeUntilTarget = currentStats.mCurrentRun.mDuration - pastTime;

        final String[] sTimeUntilTarget = TimeHelper.generateStringTimeFromMillis(timeUntilTarget, false, res);
        final String[] sTimeDuration = TimeHelper.generateStringTimeFromMillis(currentStats.mCurrentRun.mDuration, false, res);


        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_dashclock)
                .status(sTimeUntilTarget[0])
                .expandedTitle(res.getString(R.string.dashclock_at_peace))
                .expandedBody(sTimeUntilTarget[0]
                        + TEXT_SPACER
                        + sTimeUntilTarget[1]
                        + TEXT_SPACER
                        + sTimeDuration[0]
                )
                .clickIntent(getOnClickIntent()));
    }

    private PeaceOfMindPrefs getCurrentStats() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return PeaceOfMindPrefs.getStatsFromSharedPreferences(sharedPreferences);
    }

    private Intent getOnClickIntent() {
        final Intent onClickIntent = new Intent(getApplicationContext(), PeaceOfMindActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return onClickIntent;
    }

    private void registerForPeaceOfMindBroadCasts() {
        if (dashclockTimerTickReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(dashclockTimerTickReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IntentFilter intentFilter = new IntentFilter(Const.PeaceOfMindActions.DASHCLOCK_TIMER_TICK);
        dashclockTimerTickReceiver = new DashclockTimerTickReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dashclockTimerTickReceiver, intentFilter);
    }

    private void unRegisterForPeaceOfMindBroadCasts() {
        if (dashclockTimerTickReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(dashclockTimerTickReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DashclockTimerTickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onUpdateData(DashClockExtension.UPDATE_REASON_PERIODIC);
        }
    }
}
