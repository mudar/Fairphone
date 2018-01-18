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

package ca.mudar.fairphone.peaceofmind.dnd

import android.content.ContextWrapper
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.util.AirplaneModeHelper
import ca.mudar.fairphone.peaceofmind.util.CompatHelper

abstract class PeaceOfMindController(val context: ContextWrapper) {
    protected var userPrefs = UserPrefs(context)

    abstract fun startPeaceOfMind()

    abstract fun endPeaceOfMind()

    abstract fun isPeaceOfMindOn(): Boolean

    abstract fun revertAtPeaceDndMode()

    fun revertAtPeaceOfflineMode() {
        if (CompatHelper.isAtPeaceOfflineMode(context)) {
            AirplaneModeHelper.endAtPeaceOfflineMode(context)
        }
    }

    open fun setTotalSilenceMode() {}

    open fun setAlarmsOnlyMode() {}

    open fun setPriorityOnlyMode() {}

    open fun setSilentRingerMode() {}

    open fun setPriorityRingerMode() {}

    open fun setAtPeaceOfflineMode() {}
}
