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

package ca.mudar.fairphone.peaceofmind.dnd

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioManager
import ca.mudar.fairphone.peaceofmind.service.SystemNotificationListenerService
import ca.mudar.fairphone.peaceofmind.util.AirplaneModeHelper

class AudioManagerController(context: ContextWrapper) : PeaceOfMindController(context) {
    private val TAG = "AudioController"

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun startPeaceOfMind() {
        if (!isPeaceOfMindOn()) {
            userPrefs.setPreviousNoisyMode(audioManager.ringerMode)
            userPrefs.setAtPeace(true)
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

            if (userPrefs.hasNotificationListener()) {
                context.startService(Intent(context, SystemNotificationListenerService::class.java))
            }

            if (userPrefs.hasAirplaneMode()) {
                AirplaneModeHelper.startAtPeaceOfflineMode(context)
            }
        }
    }

    override fun endPeaceOfMind() {
        if (isPeaceOfMindOn()) {
            userPrefs.setAtPeace(false)
            revertAtPeaceDndMode()
            revertAtPeaceOfflineMode()
        }
    }

    override fun revertAtPeaceDndMode() {
        audioManager.ringerMode = userPrefs.getPreviousNoisyMode()
    }

    override fun isPeaceOfMindOn(): Boolean {
        return userPrefs.isAtPeace() && (userPrefs.getAtPeaceMode() == audioManager.ringerMode)
    }
}
