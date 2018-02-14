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

@RunWith(AndroidJUnit4::class)
class UserPrefsInstrumentedTest {
    lateinit var userPrefs: UserPrefs

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getTargetContext()
        userPrefs = UserPrefs(ContextWrapper(appContext))
    }

    @Test
    fun defaultPrefs() {

        Assert.assertEquals(true,
                userPrefs.hasSplashScreen())
        Assert.assertEquals(true,
                userPrefs.hasUsageHint())

        Assert.assertEquals(3,
                userPrefs.getMaxDuration())

        Assert.assertEquals(false,
                userPrefs.isAtPeace())

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

        Assert.assertEquals(false,
                userPrefs.isAtPeaceOfflineMode())
        Assert.assertEquals(false,
                userPrefs.hasAirplaneMode())

        Assert.assertEquals(false,
                userPrefs.hasEndNotification())
    }
}
