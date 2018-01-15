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

import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.widget.Toast
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import com.stericson.RootShell.exceptions.RootDeniedException
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import java.io.IOException
import java.util.concurrent.TimeoutException

object SuperuserHelper {
    private val TAG = "SuperuserHelper"

    private val COMMAND_AIRPLANE_ON = arrayOf("settings put global airplane_mode_on 1",
            "am broadcast -a " + Const.ActionNames.AIRPLANE_MODE_CHANGED +
                    " --ez state true -e " + Const.BundleKeys.AT_PEACE_STATE + " true " +
                    " -e " + Const.BundleKeys.AT_PEACE_TOGGLE + " true")
    private val COMMAND_AIRPLANE_OFF = arrayOf("settings put global airplane_mode_on 0",
            "am broadcast -a " + Const.ActionNames.AIRPLANE_MODE_CHANGED +
                    " --ez state false -e " + Const.BundleKeys.AT_PEACE_STATE + " false " +
                    " -e " + Const.BundleKeys.AT_PEACE_TOGGLE + " true")

    fun initialAccessRequest(context: ContextWrapper) {
        val thread = object : Thread() {
            override fun run() {
                val userPrefs = UserPrefs(context)
                val hasAirplaneMode = userPrefs.hasAirplaneMode()
                if (hasAirplaneMode) {
                    val isAccessGiven = RootTools.isAccessGiven(3000, 0)
                    userPrefs.setRootAccess(isAccessGiven)
                    if (!isAccessGiven) {
                        userPrefs.setAirplaneMode(false)
                    }
                }
            }
        }
        thread.start()
    }

    fun setAirplaneModeSettings(context: Context, enabled: Boolean) {
        Handler().post {
            val userPrefs = UserPrefs(ContextWrapper(context))
            val hasRootAccess = userPrefs.hasRootAccess()
            if (hasRootAccess) {
                runShellCommand(context, enabled)
            } else {
                userPrefs.setAirplaneMode(false)
            }
        }
    }

    private fun runShellCommand(context: Context?, enabled: Boolean) {
        val command = when (enabled) {
            true -> COMMAND_AIRPLANE_ON
            false -> COMMAND_AIRPLANE_OFF
        }

        val shellCommand = object : Command(0, true, *command) {

            override fun commandCompleted(i: Int, i2: Int) {
                if (enabled && context != null) {
                    Toast.makeText(context, R.string.airplane_mode_enabled_msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        try {
            RootTools.getShell(true).add(shellCommand)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        } catch (e: RootDeniedException) {
            e.printStackTrace()
        }
    }
}
