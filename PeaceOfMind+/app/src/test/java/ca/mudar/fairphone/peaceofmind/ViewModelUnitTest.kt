package ca.mudar.fairphone.peaceofmind

import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when` as whenMock


class ViewModelUnitTest {
    val viewModel = AtPeaceViewModel()

    @Before
    fun setup() {
    }

    @Test
    fun initialState() {
        Assert.assertEquals(R.string.app_name,
                viewModel.title.get())

        Assert.assertEquals(36, // 36 = 3 hours * 60 min / 5 min granularity
                viewModel.maxDuration.get())
        Assert.assertEquals(false,
                viewModel.isAtPeace.get())
        Assert.assertEquals(0,
                viewModel.seekBarProgress.get())
        Assert.assertEquals(0,
                viewModel.progressBarSweepAngle.get())
        Assert.assertEquals(0,
                viewModel.progressBarProgress.get())
        Assert.assertEquals(null,
                viewModel.duration.get())
        Assert.assertEquals(null,
                viewModel.endTime.get())
        Assert.assertEquals(false,
                viewModel.hasAirplaneMode.get())
        Assert.assertEquals(false,
                viewModel.isAtPeaceOfflineMode.get())

        // TODO: mockito, Build.VERSION.SDK_INT
//      Assert.assertEquals(23,
//              AtPeaceViewModel().atPeaceMode.get())
    }
}
