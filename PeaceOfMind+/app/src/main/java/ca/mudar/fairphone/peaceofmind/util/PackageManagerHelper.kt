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

package ca.mudar.fairphone.peaceofmind.util

import android.content.ComponentName
import android.content.ContextWrapper
import android.content.pm.PackageManager
import ca.mudar.fairphone.peaceofmind.receiver.SystemBroadcastReceiver

object PackageManagerHelper {

    fun setSystemReceiverState(context: ContextWrapper, enabled: Boolean) {
        val state = when (enabled) {
            true -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            false -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        val componentName = ComponentName(context, SystemBroadcastReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(componentName,
                state,
                PackageManager.DONT_KILL_APP)
    }

//    fun getSystemReceiverState(context: ContextWrapper): Boolean {
//        val componentName = ComponentName(context, SystemBroadcastReceiver::class.java)
//        val state = context.packageManager.getComponentEnabledSetting(componentName)
//
//        return when (state) {
//            COMPONENT_ENABLED_STATE_ENABLED -> true
//
//            COMPONENT_ENABLED_STATE_DEFAULT,
//            COMPONENT_ENABLED_STATE_DISABLED,
//            COMPONENT_ENABLED_STATE_DISABLED_USER,
//            COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> false
//            else -> false
//        }
//    }
}