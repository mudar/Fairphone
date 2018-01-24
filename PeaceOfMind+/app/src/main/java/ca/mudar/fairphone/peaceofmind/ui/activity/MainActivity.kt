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

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.ActionNames
import ca.mudar.fairphone.peaceofmind.Const.FragmentTags
import ca.mudar.fairphone.peaceofmind.Const.RequestCodes
import ca.mudar.fairphone.peaceofmind.PeaceOfMindApp
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.bus.AppEvents
import ca.mudar.fairphone.peaceofmind.bus.EventBusListener
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.databinding.ActivityMainBinding
import ca.mudar.fairphone.peaceofmind.model.AtPeaceRun
import ca.mudar.fairphone.peaceofmind.model.DisplayMode
import ca.mudar.fairphone.peaceofmind.service.AtPeaceForegroundService
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.ui.dialog.DndModesDialogFragment
import ca.mudar.fairphone.peaceofmind.util.BlueSnackbar
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.util.LogUtils
import ca.mudar.fairphone.peaceofmind.util.RefreshProgressBarTimer
import ca.mudar.fairphone.peaceofmind.util.TimeHelper
import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel
import com.squareup.otto.Subscribe
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Date

class MainActivity : BaseActivity(),
        EventBusListener,
        RefreshProgressBarTimer.TimerCallbacks {
    private val TAG = "MainActivity"

    lateinit var viewModel: AtPeaceViewModel
    lateinit var progressBarTimer: RefreshProgressBarTimer
    private var snackbar: Snackbar? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val navigator = object : MainActivity.MainNavigator {
        override fun onDndModesClick() {
            DndModesDialogFragment.newInstance().show(supportFragmentManager, FragmentTags.DND_MODE)
        }
    }

    // TODO("This should be refactored for two-way data binding")
    private val seekBarListener = object : SeekArc.OnSeekArcChangeListener {
        var startTime: Long? = null
        var displayMode: String = DisplayMode._DEFAULT
        var initialProgress: Int? = null

        override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
            viewModel.setSeekBarProgress(progress, startTime, fromUser)
        }

        override fun onStartTrackingTouch(seekArc: SeekArc?) {
            val userPrefs = UserPrefs(ContextWrapper(applicationContext))
            startTime = userPrefs.getAtPeaceRun().startTime
            displayMode = userPrefs.getDisplayMode()
            initialProgress = seekArc?.progress
            progressBarTimer.cancel()

            toggleUsageHintIfAvailable(false)
        }

        override fun onStopTrackingTouch(seekArc: SeekArc?) {
            val progress = seekArc?.progress ?: return
            if (progress == initialProgress) {
                return
            }

            when (shouldStartPeaceOfMind(progress)) {
                true -> {
                    startService(AtPeaceForegroundService
                            .newIntent(applicationContext, ActionNames.AT_PEACE_SERVICE_START))
                    progressBarTimer.start()
                }
                false -> startService(AtPeaceForegroundService
                        .newIntent(applicationContext, ActionNames.AT_PEACE_SERVICE_END))
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

        requestPermissionsIfNecessary()

        // Initialize
        progressBarTimer = RefreshProgressBarTimer(ContextWrapper(this), this)

        // ViewModel
        viewModel = ViewModelProviders.of(this).get(AtPeaceViewModel::class.java)
        viewModel.loadData(UserPrefs(this))

        // Binding
        val binding: ActivityMainBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.navigator = navigator

        // Setup views and check necessary intents
        setupViews()

        toggleUsageHintIfAvailable(true)
    }

    override fun onResume() {
        super.onResume()

        registerEventBus()

        progressBarTimer.start()

        viewModel.updateDisplayOnResume()
    }

    override fun onPause() {
        super.onPause()

        unregisterEventBus()

        progressBarTimer.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCodes.SPLASH_ACTIVITY && resultCode != Activity.RESULT_OK) {
            // Exit app if required permission is not granted
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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

    @SuppressLint("NewApi")
    private fun requestPermissionsIfNecessary() {
        if (Const.SUPPORTS_LOLLIPOP) {
            if (UserPrefs(ContextWrapper(this)).hasSplashScreen()) {
                startActivityForResult(SplashActivity.newIntent(applicationContext),
                        RequestCodes.SPLASH_ACTIVITY)
            } else {
                CompatHelper.showRequiredPermissionIfNecessary(this)
            }
        }
    }

    private fun toggleUsageHintIfAvailable(show: Boolean) {
        when (show) {
            true -> {
                if (UserPrefs(ContextWrapper(this)).hasUsageHint()) {
                    snackbar = BlueSnackbar
                            .make(main_content,
                                    R.string.msg_usage_hint,
                                    Snackbar.LENGTH_INDEFINITE
                            )
                            .setAction(R.string.btn_got_it, { toggleUsageHintIfAvailable(false) })
                    snackbar!!.show()
                }
            }
            false -> {
                UserPrefs(ContextWrapper(this)).setHasUsageHint(false)
                snackbar?.dismiss()
                snackbar = null
            }
        }
    }

    /**
     * Implements RefreshElapsedTimeTask.TimerCallbacks
     */
    override fun onTick() {
        viewModel.updateProgressBarProgress()
    }

    @Subscribe
    fun onRootAccessDenied(event: AppEvents.RootAccessDenied) {
        UserPrefs(ContextWrapper(this)).setAirplaneMode(false)
    }

    interface MainNavigator {
        fun onDndModesClick()
    }
}
