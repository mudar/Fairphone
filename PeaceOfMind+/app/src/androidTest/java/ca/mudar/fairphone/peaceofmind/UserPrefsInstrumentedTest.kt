package ca.mudar.fairphone.peaceofmind

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import ca.mudar.fairphone.peaceofmind.Const.PrefsNames
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.util.TimeHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class UserPrefsInstrumentedTest {
    lateinit var userPrefs: UserPrefs

    private val D_01_30_00_000 = 5400000L
    private val D_03_00_00_000 = 10800000L
    private val D_05_00_00_000 = 18000000L

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getTargetContext()
        userPrefs = UserPrefs(ContextWrapper(context))
        resetPrefs(context)
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
        setAtPeaceRun(D_05_00_00_000)
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
        setAtPeaceRun()

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

    private fun setAtPeaceRun(duration: Long = D_01_30_00_000) {
        userPrefs.setAtPeaceRun(AtPeaceRun(duration,
                TimeHelper.getEndTimeForDuration(duration, Date().time)
        ))
    }

    private fun resetPrefs(context: Context) {
        context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE).edit()
                .remove(PrefsNames.MAX_DURATION)
                .remove(PrefsNames.HAS_AIRPLANE_MODE)
                .remove(PrefsNames.HAS_END_NOTIFICATION)
                .remove(PrefsNames.NOTIFICATION_VIBRATE)
                .remove(PrefsNames.NOTIFICATION_RINGTONE)
                .remove(PrefsNames.NOTIFICATION_CHANNEL_SETTINGS)
                .remove(PrefsNames.NOTIFICATION_LISTENER_PERMS)
                .remove(PrefsNames.DND_PERMS)
                .remove(PrefsNames.BATTERY_OPTIMIZATION_PERMS)
                .remove(PrefsNames.CATEGORY_NOTIFICATIONS)
                .remove(PrefsNames.HAS_SPLASH)
                .remove(PrefsNames.HAS_USAGE_HINT)
                .remove(PrefsNames.IS_ROOT_AVAILABLE)
                .remove(PrefsNames.IS_AT_PEACE)
                .remove(PrefsNames.DISPLAY_MODE)
                .remove(PrefsNames.AT_PEACE_MODE)
                .remove(PrefsNames.AT_PEACE_OFFLINE_MODE)
                .remove(PrefsNames.PREVIOUS_NOISY_MODE)
                .remove(PrefsNames.PREVIOUS_AIRPLANE_MODE)
                .remove(PrefsNames.HAS_NOTIFICATION_LISTENER)
                .remove(PrefsNames.AT_PEACE_DURATION)
                .remove(PrefsNames.AT_PEACE_END_TIME)
                .commit()

        UserPrefs.setDefaultPrefs(ContextWrapper(context))
    }
}
