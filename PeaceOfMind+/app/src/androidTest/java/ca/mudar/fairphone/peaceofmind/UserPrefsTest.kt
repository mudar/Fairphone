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

import android.content.ContextWrapper
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class UserPrefsTest {
    lateinit var userPrefs: UserPrefs

    private val D_03_00_00_000 = 10800000L
    private val D_05_00_00_000 = 18000000L

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getTargetContext()
        userPrefs = UserPrefs(ContextWrapper(context))
        AppTestUtils.resetPrefs(context)
    }

    @Test
    fun defaultPrefs() {
        Assert.assertTrue(userPrefs.hasSplashScreen())
        Assert.assertTrue(userPrefs.hasUsageHint())

        Assert.assertEquals(3,
                userPrefs.getMaxDuration())

        Assert.assertFalse(userPrefs.isAtPeace())

        Assert.assertEquals(DisplayMode.DURATION,
                userPrefs.getDisplayMode())

        val atPeaceMode = when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) -> 3
            else -> 0
        }
        Assert.assertEquals(atPeaceMode,
                userPrefs.getAtPeaceMode())

        val atPeaceRun = userPrefs.getAtPeaceRun()
        Assert.assertNull(atPeaceRun.endTime)
        Assert.assertNull(atPeaceRun.duration)
        Assert.assertNull(atPeaceRun.startTime)

        Assert.assertFalse(userPrefs.isAtPeaceOfflineMode())
        Assert.assertFalse(userPrefs.hasAirplaneMode())

        Assert.assertFalse(userPrefs.hasEndNotification())
    }

    @Test
    fun toggleDisplayMode() {
        userPrefs.setDisplayMode(DisplayMode.DURATION)

        userPrefs.toggleDisplayMode()
        Assert.assertEquals(DisplayMode.END_TIME,
                userPrefs.getDisplayMode())

        userPrefs.toggleDisplayMode()
        Assert.assertEquals(DisplayMode.DURATION,
                userPrefs.getDisplayMode())
    }

    @Test
    fun clipDurationToMax() {
        // Set max duration to 6h
        userPrefs.setMaxDuration(6)
        // Set atPeace duration to 5h
        AppTestUtils.setAtPeaceRun(userPrefs, D_05_00_00_000)
        Assert.assertEquals(D_05_00_00_000,
                userPrefs.getAtPeaceRun().duration)

        // Set max duration to 6h
        userPrefs.setMaxDuration(3)
//        Assert.assertNotEquals(D_03_00_00_000,
//                userPrefs.getAtPeaceRun().duration)

        // Check duration and clip if necessary
        val isClipped = userPrefs.isAtPeaceDurationClipped(Date().time)

        // Check that clip was necessary, with fixed new duration (3h)
        Assert.assertTrue(isClipped)
        Assert.assertEquals(D_03_00_00_000,
                userPrefs.getAtPeaceRun().duration)
    }

    @Test
    fun clearAtPeaceRun() {
        userPrefs.setAtPeace(true)
        AppTestUtils.setAtPeaceRun(userPrefs)

        Assert.assertTrue(userPrefs.isAtPeace())

        userPrefs.setAtPeace(false)

        val atPeaceRun = userPrefs.getAtPeaceRun()
        Assert.assertNull(atPeaceRun.endTime)
        Assert.assertNull(atPeaceRun.duration)
        Assert.assertNull(atPeaceRun.startTime)
    }

    @Test
    fun atPeaceOfflineMode() {
        userPrefs.setAirplaneMode(true)
        userPrefs.setAtPeaceMode(1, true)
        Assert.assertTrue(userPrefs.isAtPeaceOfflineMode())

        userPrefs.setAirplaneMode(true)
        userPrefs.setAtPeaceMode(1, false)
        Assert.assertFalse(userPrefs.isAtPeaceOfflineMode())

        // AtPeace mode cannot be offline if Airplane mode is disabled in settings
        userPrefs.setAirplaneMode(false)
        userPrefs.setAtPeaceMode(1, true)
        Assert.assertFalse(userPrefs.isAtPeaceOfflineMode())
    }

}
