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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.R;
import ca.mudar.fairphone.peaceofmind.superuser.SuperuserHelper;

public class AirplaneModeDeviceController extends SilentModeDeviceController {


    public AirplaneModeDeviceController(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        mContext = context;
    }

    private void setAirplaneModeSettings(int value) {
        if (Const.SUPPORTS_JELLY_BEAN_MR1) {
            // For API-17, we rely on Superuser. This includes the sendAirplaneModeIntent() call
            SuperuserHelper.setAirplaneModeSettings(mContext, value);
        } else {
            Settings.System.putInt(
                    mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, value);
            final boolean isEnabled = (value == 1);
            if (isEnabled) {
                Toast.makeText(mContext, R.string.airplane_mode_enabled, Toast.LENGTH_SHORT).show();
            }
            sendAirplaneModeIntent(isEnabled);
        }
    }

    private void sendAirplaneModeIntent(boolean isEnabled) {
        // For API-17, we rely on Superuser in the sendAirplaneModeIntent() call
        if (!Const.SUPPORTS_JELLY_BEAN_MR1) {
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra(Const.PeaceOfMindIntents.EXTRA_STATE, isEnabled);
            intent.putExtra(Const.PeaceOfMindIntents.EXTRA_TOGGLE, true);
            try {
                mContext.sendBroadcast(intent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startPeaceOfMind() {
        // Don't check for isPeaceOfMindOn() here. This disables WiFi if already in
        // AirplaneMode with WiFi enabled (special case)
        setRingerMode(1, false);
        setAirplaneModeSettings(1);
    }

    @Override
    public void endPeaceOfMind() {
        if (isPeaceOfMindOn()) {
            setRingerMode(0, false);
            setAirplaneModeSettings(0);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean isPeaceOfMindOn() {
        if (Const.SUPPORTS_JELLY_BEAN_MR1) {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

}
