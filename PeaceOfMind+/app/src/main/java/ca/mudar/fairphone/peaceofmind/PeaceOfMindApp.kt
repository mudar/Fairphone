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

import android.app.Application
import android.content.ContextWrapper
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer


class PeaceOfMindApp : Application() {

    companion object {
        val eventBus = Bus(ThreadEnforcer.ANY)
    }

    override fun onCreate() {
        super.onCreate()

        setupCrashlytics()

        // Load default sharedPrefs
        UserPrefs.setDefaultPrefs(ContextWrapper(this))
    }

    private fun setupCrashlytics() {
        if (BuildConfig.USE_CRASHLYTICS) {
            // Fabric.with(this, Crashlytics())
        }
    }
}
