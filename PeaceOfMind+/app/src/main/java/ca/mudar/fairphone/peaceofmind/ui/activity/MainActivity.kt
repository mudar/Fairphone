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
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import ca.mudar.fairphone.peaceofmind.BuildConfig
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.databinding.ActivityMainBinding
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.ui.dialog.HelpDialogFragment
import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    private val tag = "MainActivity"

    lateinit var viewModel: AtPeaceViewModel
    lateinit var userPrefs: UserPrefs

    // TODO("This should be refactored for two-way data binding")
    private val listener = object : SeekArc.OnSeekArcChangeListener {
        override fun onProgressChanged(seekArc: SeekArc?, progress: Int, fromUser: Boolean) {
            viewModel.setSeekBarProgress(progress)
        }

        override fun onStartTrackingTouch(seekArc: SeekArc?) {
        }

        override fun onStopTrackingTouch(seekArc: SeekArc?) {
            val progress = seekArc?.progress ?:
                    return
            userPrefs.setAtPeace(progress > 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AtPeaceViewModel::class.java)
        viewModel.loadData(ContextWrapper(this))

        val binding: ActivityMainBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewModel

        UserPrefs.setDefaultPrefs(ContextWrapper(this))
        userPrefs = UserPrefs(ContextWrapper(this))

        setupToolbar()

        setupListeners()

        showSplashOnFirstLaunch()
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
        // TODO remove this, for debug only
            R.id.action_anim_on -> {
                playAnimOn()
                true
            }
            R.id.action_anim_off -> {
                playAnimOff()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    @Deprecated("Remove this: temporary implementation of play-anim")
    private fun playAnimOn() {
        bg_anim.setImageResource(R.drawable.water_fill_anim)
        val anim = bg_anim.drawable as Animatable
        anim.start()

        progress_bar.postDelayed({ progress_bar.visibility = View.VISIBLE },
                1500)
    }

    @Deprecated("Remove this: temporary implementation of play-anim")
    private fun playAnimOff() {
        bg_anim.setImageResource(R.drawable.water_purge_anim)
        val anim = bg_anim.drawable as Animatable
        anim.start()
    }

    private fun showSplashOnFirstLaunch() {
        if (userPrefs.isFirstLaunch()) {
            showHelpBottomSheet()
        }
    }

    private fun showHelpBottomSheet() {
        val bottomSheet = HelpDialogFragment.newInstance()

        bottomSheet.show(supportFragmentManager, Const.FragmentTags.HELP)
    }

    private fun setupListeners() {
        seek_bar.setOnSeekArcChangeListener(listener)
    }
}
