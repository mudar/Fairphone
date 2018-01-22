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

package ca.mudar.fairphone.peaceofmind.ui.activity

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.PeaceOfMindApp
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.bus.AppEvents
import ca.mudar.fairphone.peaceofmind.bus.EventBusListener
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.ui.fragment.SettingsFragment
import ca.mudar.fairphone.peaceofmind.util.BlueSnackbar
import ca.mudar.fairphone.peaceofmind.util.LogUtils
import com.squareup.otto.Subscribe


class SettingsActivity : BaseActivity(),
        EventBusListener {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableCompatVectorResourcesIfNecessary()

        if (savedInstanceState == null) {
            val fragment = SettingsFragment.newInstance()
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment, Const.FragmentTags.SETTINGS)
                    .commit()
        }

        registerEventBus()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterEventBus()
    }

    /**
     * Implements EventBusListener
     */
    override fun registerEventBus() {
        try {
            PeaceOfMindApp.eventBus.register(this)
        } catch (e: IllegalArgumentException) {
            LogUtils.REMOTE_LOG(e)
        }
    }

    /**
     * Implements EventBusListener
     */
    override fun unregisterEventBus() {
        try {
            PeaceOfMindApp.eventBus.unregister(this)
        } catch (e: IllegalArgumentException) {
            LogUtils.REMOTE_LOG(e)
        }
    }

    @Subscribe
    fun onRootAccessDenied(event: AppEvents.RootAccessDenied) {
        UserPrefs(ContextWrapper(this)).setAirplaneMode(false)
    }

    @Subscribe
    fun onRootAvailabilityDetected(event: AppEvents.RootAvailabilityDetected) {
        UserPrefs(ContextWrapper(this)).setRootAvailable(true)

        BlueSnackbar
                .make(findViewById(android.R.id.content),
                        R.string.msg_restart_app_for_root_settings,
                        Snackbar.LENGTH_INDEFINITE
                )
                .setAction(R.string.btn_restart, { restartSettingsActivity() })
                .show()
    }

    private fun restartSettingsActivity() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(newIntent(this))
    }
}
