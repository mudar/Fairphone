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

package ca.mudar.fairphone.peaceofmind.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.dnd.PeaceOfMindController
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.util.LogUtils

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "AlarmBroadcastReceiver"
    lateinit var peaceOfMindController: PeaceOfMindController

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = Const.ActionNames.TIMER_EXPIRED

            return intent
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.LOGV(TAG, "onReceive, action = " + intent?.action)
        context?.let {
            peaceOfMindController = CompatHelper.getPeaceOfMindController(ContextWrapper(context))
            peaceOfMindController.endPeaceOfMind()
        }
    }
}