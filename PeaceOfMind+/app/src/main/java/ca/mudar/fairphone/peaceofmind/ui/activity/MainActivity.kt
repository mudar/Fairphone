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

import android.arch.lifecycle.ViewModelProviders
import android.content.ContextWrapper
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.mudar.fairphone.peaceofmind.BuildConfig
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.PeaceOfMindApp
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.bus.EventBusListener
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.databinding.ActivityMainBinding
import ca.mudar.fairphone.peaceofmind.dnd.PeaceOfMindController
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.ui.dialog.HelpDialogFragment
import ca.mudar.fairphone.peaceofmind.util.*
import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(),
        EventBusListener,
        RefreshProgressBarTimer.TimerCallbacks {
    private val TAG = "MainActivity"

    lateinit var viewModel: AtPeaceViewModel
    lateinit var peaceOfMindController: PeaceOfMindController
    lateinit var progressBarTimer: RefreshProgressBarTimer

    // TODO("This should be refactored for two-way data binding")
    private val seekBarListener = object : SeekArc.OnSeekArcChangeListener {
        var startTime: Long? = null
        var displayMode: String = DisplayMode._DEFAULT

        override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
            viewModel.setSeekBarProgress(progress, startTime, fromUser)
        }

        override fun onStartTrackingTouch(seekArc: SeekArc?) {
            val userPrefs = UserPrefs(ContextWrapper(applicationContext))
            startTime = userPrefs.getAtPeaceRun().startTime
            displayMode = userPrefs.getDisplayMode()
            progressBarTimer.cancel()
        }

        override fun onStopTrackingTouch(seekArc: SeekArc?) {
            val progress = seekArc?.progress ?:
                    return

            when (shouldStartPeaceOfMind(progress)) {
                true -> {
                    peaceOfMindController.startPeaceOfMind()
                    progressBarTimer.start()
                }
                false -> peaceOfMindController.endPeaceOfMind()
            }
        }

        private fun shouldStartPeaceOfMind(progress: Int): Boolean {
            if (progress <= 0) {
                return false
            }

            val userPrefs = UserPrefs(ContextWrapper(applicationContext))
            val duration = TimeHelper.getDurationForProgress(progress,
                    displayMode,
                    startTime)
            val endTime = TimeHelper.getEndTimeForDuration(duration, startTime)

            return if (userPrefs.isAtPeace() && endTime <= Date().time) {
                false
            } else {
                userPrefs.setAtPeaceRun(AtPeaceRun(duration = duration, endTime = endTime))
                true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize
        peaceOfMindController = CompatHelper.getPeaceOfMindController(ContextWrapper(this))
        progressBarTimer = RefreshProgressBarTimer(ContextWrapper(this), this)

        // ViewModel
        viewModel = ViewModelProviders.of(this).get(AtPeaceViewModel::class.java)
        viewModel.loadData(UserPrefs(this))

        // Binding
        val binding: ActivityMainBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.peaceOfMindController = peaceOfMindController

        // Setup views and check necessary intents
        setupViews()
        showSplashOnFirstLaunch()
        checkPermissions()
    }

    override fun onResume() {
        super.onResume()

        registerEventBus()

        progressBarTimer.start()
    }

    override fun onPause() {
        super.onPause()

        unregisterEventBus()

        progressBarTimer.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (BuildConfig.DEBUG) {
            // TODO remove this, for debug only
            menuInflater.inflate(R.menu.menu_debug, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                showHelpBottomSheet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun setupViews() {
        setSupportActionBar(toolbar)

        seek_bar.setOnSeekArcChangeListener(seekBarListener)
    }

    private fun showSplashOnFirstLaunch() {
        if (UserPrefs(ContextWrapper(this)).isFirstLaunch()) {
            showHelpBottomSheet()
        }
    }

    private fun showHelpBottomSheet() {
        val bottomSheet = HelpDialogFragment.newInstance()

        bottomSheet.show(supportFragmentManager, Const.FragmentTags.HELP)
    }

    private fun checkPermissions() {
        PermissionsManager.requestNotificationsPolicyAccess(this)
    }

    /**
     * Implements RefreshElapsedTimeTask.TimerCallbacks
     */
    override fun onTick() {
        viewModel.updateProgressBarProgress()
    }
}
