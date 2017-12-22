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

package ca.mudar.fairphone.peaceofmind.io

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioManager
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService
import ca.mudar.fairphone.peaceofmind.util.LogUtils

class AudioManagerController(private val context: ContextWrapper) : PeaceOfMindController {
    private val TAG = "AudioController"

    private var userPrefs = UserPrefs(context)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun startPeaceOfMind() {
        if (!isPeaceOfMindOn()) {
            UserPrefs(context).setPreviousNoisyMode(audioManager.ringerMode)
            userPrefs.setAtPeace(true)
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

            if (UserPrefs(ContextWrapper(context)).hasNotificationListener()) {
                context.startService(Intent(context, SystemNotificationListenerService::class.java))
            }
        }
    }

    override fun endPeaceOfMind() {
        if (isPeaceOfMindOn()) {
            val previousNoisyMode = UserPrefs(context).getPreviousNoisyMode()
            userPrefs.setAtPeace(false)
            audioManager.ringerMode = previousNoisyMode
        }
    }

    // TODO("clear timer here")
    override fun forceEndPeaceOfMind() {
        userPrefs.setAtPeace(false)
        LogUtils.LOGV(TAG, "TODO: clear timer here")
    }

    override fun isPeaceOfMindOn(): Boolean {
        return userPrefs.isAtPeace() && (userPrefs.getAtPeaceMode() == audioManager.ringerMode)
    }

    override fun setSilentRingerMode() {
        setAtPeaceMode(AudioManager.RINGER_MODE_SILENT)
    }

    override fun setPriorityRingerMode() {
        setAtPeaceMode(AudioManager.RINGER_MODE_NORMAL)
    }

    override fun setTotalSilenceMode() {
        // Nothing to do here
    }

    override fun setAlarmsOnlyMode() {
        // Nothing to do here
    }

    override fun setPriorityOnlyMode() {
        // Nothing to do here
    }

    private fun setAtPeaceMode(mode: Int) {
        if (isPeaceOfMindOn()) {
            audioManager.ringerMode = mode
        }
        userPrefs.setAtPeaceMode(mode)
    }
}
