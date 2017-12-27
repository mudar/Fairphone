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
import ca.mudar.fairphone.peaceofmind.util.AlarmManagerHelper

interface PeaceOfMindController {
    val context: ContextWrapper

    fun startPeaceOfMind() {
        AlarmManagerHelper(context).set()
    }

    fun endPeaceOfMind() {
        AlarmManagerHelper(context).cancel()
    }

    fun forceEndPeaceOfMind() {
        AlarmManagerHelper(context).cancel()
    }

    fun isPeaceOfMindOn(): Boolean

    fun setTotalSilenceMode()

    fun setAlarmsOnlyMode()

    fun setPriorityOnlyMode()

    fun setSilentRingerMode()

    fun setPriorityRingerMode()
}
