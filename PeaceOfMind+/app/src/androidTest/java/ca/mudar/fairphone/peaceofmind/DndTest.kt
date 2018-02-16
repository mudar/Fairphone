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

package ca.mudar.fairphone.peaceofmind

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.receiver.SystemBroadcastReceiver
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DndTest {
    lateinit var userPrefs: UserPrefs
    lateinit var context: Context

    private val D_01_30_00_000 = 5400000L

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        userPrefs = UserPrefs(ContextWrapper(context))
        AppTestUtils.resetPrefs(context)
    }

    @Test
    fun toggleAtPeace() {
        // Initially, atPeace is off
        Assert.assertFalse(userPrefs.isAtPeace())

        // Start atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        // End atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_END)
        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByRingerChange() {
        // Start atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        when {
            Const.SUPPORTS_MARSHMALLOW -> {
                // Disable DND
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
            Const.SUPPORTS_LOLLIPOP -> {
                // Force receiver revert action by setting mode value to `-1`
                userPrefs.setAtPeaceMode(-1, false)
                SystemBroadcastReceiver().onReceive(context,
                        Intent(CompatHelper.getRingerModeChangedActionName()))
            }
            else -> {
                // Change ringer mode
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE)
                        as AudioManager
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }
        Thread.sleep(2000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByReboot() {
        // Start atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(Const.ActionNames.REBOOT))
        Thread.sleep(2000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByShutdown() {
        // Start atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(Const.ActionNames.SHUTDOWN))
        Thread.sleep(2000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByAirplaneModeChange() {
        // Start atPeace
        AppTestUtils.startAtPeaceService(context, Const.ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(Const.ActionNames.AIRPLANE_MODE_CHANGED))
        Thread.sleep(2000)

        userPrefs.setAirplaneMode(false)

        // Airplane mode change is ignored if setting is not enabled
        Assert.assertTrue(userPrefs.isAtPeace())

        // Enable Airplane mode setting
        userPrefs.setAirplaneMode(true)

        SystemBroadcastReceiver().onReceive(context, Intent(Const.ActionNames.AIRPLANE_MODE_CHANGED))
        Thread.sleep(2000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }
}