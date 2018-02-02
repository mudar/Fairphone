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

import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.PeaceOfMindApp
import ca.mudar.fairphone.peaceofmind.bus.AppEvents
import com.stericson.RootShell.RootShell
import com.stericson.RootShell.exceptions.RootDeniedException
import com.stericson.RootShell.execution.Command
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

    fun isAccessGiven() {
        val thread = object : Thread() {
            // TODO verify thread lifecycle (vs timeout)
            override fun run() {
                val isAccessGiven = RootShell.isAccessGiven(0, 0)

                when (isAccessGiven) {
                    true -> PeaceOfMindApp.eventBus.post(AppEvents.RootAccessGranted())
                    false -> PeaceOfMindApp.eventBus.post(AppEvents.RootAccessDenied())
                }
            }
        }
        thread.start()
    }

    fun checkRootAvailability() {
        val thread = object : Thread() {
            override fun run() {
                if (RootShell.isRootAvailable()) {
                    PeaceOfMindApp.eventBus.post(AppEvents.RootAvailabilityDetected())
                }
            }
        }
        thread.start()
    }

    fun setAirplaneModeSettings(enabled: Boolean) {
        val thread = object : Thread() {
            // TODO verify thread lifecycle (vs timeout)
            override fun run() {
                val hasRootAccess = RootShell.isAccessGiven(0, 0)
                if (hasRootAccess) {
                    runShellCommand(enabled)
                } else {
                    PeaceOfMindApp.eventBus.post(AppEvents.RootAccessDenied())
                }
            }
        }
        thread.start()
    }

    private fun runShellCommand(enabled: Boolean) {
        val command = when (enabled) {
            true -> COMMAND_AIRPLANE_ON
            false -> COMMAND_AIRPLANE_OFF
        }

        val shellCommand = object : Command(0, true, *command) {

            override fun commandCompleted(i: Int, i2: Int) {
                when (enabled) {
                    true -> PeaceOfMindApp.eventBus.post(AppEvents.AirplaneModeEnabled())
                    false -> PeaceOfMindApp.eventBus.post(AppEvents.AirplaneModeDisabled())
                }
            }
        }

        try {
            RootShell.getShell(true).add(shellCommand)
            PeaceOfMindApp.eventBus.post(AppEvents.AirplaneModeToggleRequested())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        } catch (e: RootDeniedException) {
            e.printStackTrace()
        }
    }
}
