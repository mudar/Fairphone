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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.Const.RequestCodes
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.data.UserPrefs
import ca.mudar.fairphone.peaceofmind.databinding.ActivitySplashBinding
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.util.CompatHelper

class SplashActivity : BaseActivity() {

    private var activityResult = false

    companion object {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun newIntent(context: Context): Intent {
            return Intent(context, SplashActivity::class.java)
        }
    }

    private val navigator = object : SplashNavigator {
        override fun onGrantPermission(context: Context) {
            if (Const.SUPPORTS_LOLLIPOP) {
                CompatHelper.requestRequiredPermission(this@SplashActivity)
            } else {
                // This should not happen: filtered on newIntent call
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySplashBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_splash)
        binding.navigator = navigator

        UserPrefs(this).setHasSplashScreen(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCodes.NOTIFICATION_POLICY_ACCESS_SETTINGS ||
                requestCode == RequestCodes.NOTIFICATION_LISTENER_SETTINGS) {
            activityResult = CompatHelper.checkRequiredPermission(this)
            when (activityResult) {
                true -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else -> setResult(Activity.RESULT_CANCELED)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // If permission wasn't granted, show splash next time
        UserPrefs(this).setHasSplashScreen(!activityResult)
    }

    interface SplashNavigator {
        fun onGrantPermission(context: Context)
    }
}