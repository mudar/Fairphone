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

import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import ca.mudar.fairphone.peaceofmind.data.UserPrefs

@TargetApi(Build.VERSION_CODES.M)
class NotificationManagerController(private val context: ContextWrapper) : PeaceOfMindController {
    private val tag = "NotificationController"

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    /**
     * Implements PeaceOfMindController
     */
    override fun startPeaceOfMind() {
        if (!isPeaceOfMindOn()) {
            UserPrefs(context).setPreviousNoisyMode(notificationManager.currentInterruptionFilter)
            notificationManager.setInterruptionFilter(UserPrefs(context).getAtPeaceMode())
        }
    }

    /**
     * Implements PeaceOfMindController
     */
    override fun endPeaceOfMind() {
        if (isPeaceOfMindOn()) {
            val previousNoisyMode = UserPrefs(context).getPreviousNoisyMode()
            notificationManager.setInterruptionFilter(previousNoisyMode)
        }
    }

    /**
     * Implements PeaceOfMindController
     */
    override fun forceEndPeaceOfMind() {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    /**
     * Implements PeaceOfMindController
     */
    override fun isPeaceOfMindOn(): Boolean {
        return notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE
    }
}