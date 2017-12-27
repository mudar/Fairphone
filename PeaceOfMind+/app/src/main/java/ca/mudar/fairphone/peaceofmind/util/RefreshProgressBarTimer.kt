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

import android.content.ContextWrapper
import android.os.Handler
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.data.UserPrefs

class RefreshProgressBarTimer(context: ContextWrapper, val listener: TimerCallbacks) {

    private val userPrefs = UserPrefs(context)
    private var progressRefreshRunnable: RepeatedRunnable? = null
    private val handler = Handler()

    fun start() {
        // remove previous instances
        handler.removeCallbacks(progressRefreshRunnable)

        val duration = userPrefs.getAtPeaceRun().duration
                ?: return

        progressRefreshRunnable = RepeatedRunnable(duration / Const.SeekArc.PROGRESS_BAR_MAX)
        progressRefreshRunnable?.run()
    }

    fun cancel() {
        handler.removeCallbacks(progressRefreshRunnable)
    }

    inner class RepeatedRunnable(private val delay: Long) : Runnable {
        override fun run() {
            listener.onTick()

            handler.postDelayed(this, delay)
        }
    }

    interface TimerCallbacks {
        fun onTick()
    }
}