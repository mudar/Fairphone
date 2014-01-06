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

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

import ca.mudar.fairphone.peaceofmind.Const;
import ca.mudar.fairphone.peaceofmind.R;

public class SilentModeDeviceController implements IDeviceController {

    private Context mContext;

    public SilentModeDeviceController(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        mContext = context;
    }

    private void setRingerMode(int value) {
        final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (value == 1) {
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(mContext, R.string.silent_mode_enabled, Toast.LENGTH_SHORT).show();
            }
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    private void sendSilentModeIntent(boolean isEnabled) {
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
        if (!isPeaceOfMindOn()) {
            setRingerMode(1);
            sendSilentModeIntent(true);
        }
    }

    @Override
    public void endPeaceOfMind() {
        if (isPeaceOfMindOn()) {
            setRingerMode(0);
            sendSilentModeIntent(false);
        }
    }

    @Override
    public boolean isPeaceOfMindOn() {
        final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT);
    }
}
