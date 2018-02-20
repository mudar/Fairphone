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
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.v4.content.ContextCompat.startActivity
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.receiver.SystemBroadcastReceiver
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DndTest {
    lateinit var userPrefs: UserPrefs
    lateinit var context: Context

    @Rule
    @JvmField
    val serviceRule = ServiceTestRule()

    private val D_01_30_00_000 = 5400000L

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        userPrefs = UserPrefs(ContextWrapper(context))
        AppTestUtils.resetPrefs(context)
    }

    @Test
    fun toggleAtPeace() {
        uiRequestAndGrantPermissionIfNecessary()

        // Initially, atPeace is off
        Assert.assertFalse(userPrefs.isAtPeace())

        // Start atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        Thread.sleep(1000)

        // End atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_END)
        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByRingerChange() {
        uiRequestAndGrantPermissionIfNecessary()

        // Start atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_START)
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
        Thread.sleep(3000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByReboot() {
        uiRequestAndGrantPermissionIfNecessary()

        // Start atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(ActionNames.REBOOT))
        Thread.sleep(1000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByShutdown() {
        uiRequestAndGrantPermissionIfNecessary()

        // Start atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(ActionNames.SHUTDOWN))
        Thread.sleep(1000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    @Test
    fun endAtPeaceByAirplaneModeChange() {
        uiRequestAndGrantPermissionIfNecessary()

        userPrefs.setAirplaneMode(false)

        // Start atPeace
        startServiceIntent(ActionNames.AT_PEACE_SERVICE_START)
        Assert.assertTrue(userPrefs.isAtPeace())

        SystemBroadcastReceiver().onReceive(context, Intent(ActionNames.AIRPLANE_MODE_CHANGED))
        Thread.sleep(1000)

        // Airplane mode change is ignored if setting is not enabled
        Assert.assertTrue(userPrefs.isAtPeace())

        // Enable Airplane mode setting
        userPrefs.setAirplaneMode(true)

        SystemBroadcastReceiver().onReceive(context, Intent(ActionNames.AIRPLANE_MODE_CHANGED))
        Thread.sleep(1000)

        Assert.assertFalse(userPrefs.isAtPeace())
    }

    private fun startServiceIntent(action: String) {
        serviceRule.startService(AtPeaceForegroundService
                .newIntent(context, action))
    }

    private fun uiRequestAndGrantPermissionIfNecessary() {
        if (!CompatHelper.checkRequiredPermission(ContextWrapper(context))) {
            if (Const.SUPPORTS_MARSHMALLOW) {
                requestPermissionClickButton(ActionNames.NOTIFICATION_POLICY_ACCESS_SETTINGS,
                        "ALLOW",
                        "DENY")
            } else if (Const.SUPPORTS_LOLLIPOP) {
                UserPrefs(ContextWrapper(context)).setNotificationListener(true)
                requestPermissionClickButton(ActionNames.NOTIFICATION_LISTENER_SETTINGS,
                        "Ok",
                        "Cancel")
            }
        }

        Assert.assertTrue(CompatHelper.checkRequiredPermission(ContextWrapper(context)))
    }

    private fun requestPermissionClickButton(action: String,
                                             positiveButton: String,
                                             negativeButton: String) {
        val intent = Intent(action)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(context, intent, Bundle())
        Thread.sleep(1000)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val appListItem = device.findObject(UiSelector()
                .text(context.getString(R.string.app_name)))
        Assert.assertNotNull(appListItem.exists())
        appListItem.click()

        val grantBtn = device.findObject(UiSelector().textContains(positiveButton))
        val denyBtn = device.findObject(UiSelector().textContains(negativeButton))
        Assert.assertTrue(grantBtn.exists())
        Assert.assertTrue(denyBtn.exists())

        grantBtn.click()
    }
}
