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
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import ca.mudar.fairphone.peaceofmind.BuildConfig
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.databinding.ActivityAboutBinding
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }

    private val navigator = object : AboutActivity.AboutNavigator {
        override fun onFairphoneCreditsClick() {
            showWebsite(R.string.url_fairphone)
        }

        override fun onSourceCodeClick() {
            showWebsite(R.string.url_github)
        }

        override fun onDevCreditsClick() {
            showWebsite(R.string.url_mudar_ca)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        val binding: ActivityAboutBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_about)
        binding.navigator = navigator
        binding.versionNumber = getVersionNumber()

        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getVersionNumber(): String {
        return getString(R.string.about_version, BuildConfig.VERSION_NAME)
    }

    interface AboutNavigator {
        fun onFairphoneCreditsClick()
        fun onSourceCodeClick()
        fun onDevCreditsClick()
    }
}