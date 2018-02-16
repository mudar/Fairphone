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

import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when` as whenMock


class ViewModelTest {
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
