package ca.mudar.fairphone.peaceofmind

import android.content.Context
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.util.TimeHelper
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Random
import org.hamcrest.CoreMatchers.`is` as isMock
import org.mockito.Mockito.`when` as whenMock

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

@RunWith(MockitoJUnitRunner::class)
class TimeHelperUnitTest {

    @Mock
    lateinit var mockContext: Context

    // Tue, 6 February 2018 13:59:00.000 GMT-05:00
    private val T_13_59_00_000 = 1517943540000L
    // Tue, 6 February 2018 13:59:17.100 GMT-05:00
    private val T_13_59_17_100 = 1517943557100L
    // Tue, 6 February 2018 13:59:33.600 GMT-05:00
    private val T_13_59_33_600 = 1517943573600L

    private val D_02_14_00_000 = 8040000L
    private val D_02_14_23_100 = 8063100L
    private val D_02_14_45_700 = 8085700L
    private val D_10_06_40_300 = 36400300L

    private val R_STRING_duration_hours_minutes = "%1\$sh%2\$s"


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        whenMock(mockContext.getString(eq(R.string.duration_hours_minutes),
                ArgumentMatchers.any())).thenAnswer(Answer<String> {
            return@Answer String.format(R_STRING_duration_hours_minutes, it.arguments[1], it.arguments[2])
        })
    }

    @Test
    fun roundTime() {
        Assert.assertEquals(TimeHelper.testGetTimeWithoutSeconds(T_13_59_00_000),
                T_13_59_00_000)

        Assert.assertEquals(TimeHelper.testGetTimeWithoutSeconds(T_13_59_17_100),
                T_13_59_00_000)

        Assert.assertEquals(TimeHelper.testGetTimeWithoutSeconds(T_13_59_33_600),
                T_13_59_00_000)
    }

    @Test
    fun computeEndTime_EmptyOrNull() {
        val nowRounded = GregorianCalendar()
        nowRounded.set(Calendar.MILLISECOND, 0)
        nowRounded.set(Calendar.SECOND, 0)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(null, null),
                nowRounded.timeInMillis)
        Assert.assertEquals(TimeHelper.getEndTimeForDuration(0, null),
                nowRounded.timeInMillis)
        Assert.assertEquals(TimeHelper.getEndTimeForDuration(null, 0),
                0)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_00_000, null),
                nowRounded.timeInMillis + D_02_14_00_000)
        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_00_000, 0),
                D_02_14_00_000)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(null, T_13_59_00_000),
                T_13_59_00_000)
        Assert.assertEquals(TimeHelper.getEndTimeForDuration(0, T_13_59_00_000),
                T_13_59_00_000)
    }


    @Test
    fun computeEndTime() {
        val roundEndTime = D_02_14_00_000 + T_13_59_00_000
        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_00_000, T_13_59_00_000),
                roundEndTime)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_00_000, T_13_59_33_600),
                roundEndTime)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_45_700, T_13_59_00_000),
                roundEndTime)

        Assert.assertEquals(TimeHelper.getEndTimeForDuration(D_02_14_45_700, T_13_59_33_600),
                roundEndTime)
    }

    @Test
    fun atPeacePercentage() {
        val now = T_13_59_00_000
        val duration = TimeHelper.testGetTimeWithoutSeconds(D_02_14_45_700)
        val endTime = TimeHelper.getEndTimeForDuration(duration, now)

        Assert.assertEquals(TimeHelper.getAtPeaceElapsedPercentage(
                AtPeaceRun(duration, endTime), now + (duration / 2L)
        ), 0.5f)
        Assert.assertEquals(TimeHelper.getAtPeaceElapsedPercentage(
                AtPeaceRun(duration, endTime), now + (duration / 3L)
        ), 0.33f)
        // At the exact endTime
        Assert.assertEquals(TimeHelper.getAtPeaceElapsedPercentage(
                AtPeaceRun(duration, endTime), now + duration
        ), 1f)
        // Past endTime by one second
        Assert.assertEquals(TimeHelper.getAtPeaceElapsedPercentage(
                AtPeaceRun(duration, endTime), now + duration + 1000
        ), 1f)
        // Set in future (in one second), didn't start yet
        Assert.assertEquals(TimeHelper.getAtPeaceElapsedPercentage(
                AtPeaceRun(duration, endTime), now - 1000),
                0f)
        // Has started
        val elapsed: Long = (D_02_14_45_700 * (Random().nextFloat() + 0.1f)).toLong()
        Assert.assertNotEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(duration, endTime), now + elapsed),
                0f)
    }

    @Test
    fun atPeacePercentage_EmptyOrNull() {
        val now = Date().time

        // Returns zero, for not started
        Assert.assertEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(null, null)),
                0f)
        Assert.assertEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(null, now + D_02_14_00_000)),
                0f)
        Assert.assertEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(0, null)),
                0f)
        Assert.assertEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(0, now + D_02_14_00_000)),
                0f)
        Assert.assertEquals(TimeHelper
                .getAtPeaceElapsedPercentage(AtPeaceRun(D_02_14_00_000, null)),
                0f)
    }


    @Test
    fun readStringFromContext_LocalizedString() {
        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_10_06_40_300),
                isMock(not("10h6")))
        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_10_06_40_300),
                isMock(equalTo("10h06")))

        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_02_14_00_000),
                isMock(not("02h14")))
        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_02_14_00_000),
                isMock(equalTo("2h14")))
        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_02_14_23_100),
                isMock(equalTo("2h14")))
        Assert.assertThat(TimeHelper.getDurationLabel(mockContext, D_02_14_45_700),
                isMock(equalTo("2h14")))
    }
}
