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

package ca.mudar.fairphone.peaceofmind.ui.activity.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.ui.activity.EulaActivity
import ca.mudar.fairphone.peaceofmind.ui.activity.SettingsActivity
import com.mikepenz.aboutlibraries.LibsBuilder


abstract class BaseActivity : AppCompatActivity() {

    protected fun enableCompatVectorResourcesIfNecessary() {
        if (!Const.SUPPORTS_VECTOR_DRAWABLES) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(SettingsActivity.newIntent(applicationContext))
                true
            }
            R.id.action_share -> {
                onShareItemSelected()
                true
            }
            R.id.action_rate -> {
                showWebsite(R.string.url_playstore)
                true
            }
            R.id.action_eula -> {
                startActivity(EulaActivity.newIntent(applicationContext))
                true
            }
            R.id.action_about_libs -> {
                onAboutLibsItemSelected()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Native sharing
     */
    private fun onShareItemSelected() {
        val extras = Bundle()
        extras.putString(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_intent_title))
        extras.putString(Intent.EXTRA_TEXT, resources.getString(R.string.url_playstore))

        val sendIntent = Intent()
        sendIntent.putExtras(extras)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.type = Const.PLAIN_TEXT_MIME_TYPE
        startActivity(sendIntent)
    }

    /**
     * Show the AboutLibraries acknowledgements activity
     */
    private fun onAboutLibsItemSelected() {
        LibsBuilder()
                .withActivityTitle(getString(R.string.activity_about_libs))
                .withActivityTheme(R.style.AppTheme_AboutLibs)
//                .withAutoDetect(false) // For Proguard
                .withFields(R.string::class.java.fields) // For Proguard
//                .withLibraries(
//                        "GooglePlayServices", "huesdk", "Otto", "AboutLibraries", "Crashlytics", "gson", "OkHttp", "Retrofit", "appcompat_v7", "design", "recyclerview_v7", "materialtaptargetprompt", "draglistview"
//                )// Added manually to avoid issues with Proguard
                .withExcludedLibraries(
                        "AndroidIconics", "fastadapter", "support_v4"
                )
                .start(this)
    }

    /**
     * Launch intent to view website
     *
     * @param website
     */
    private fun showWebsite(@StringRes website: Int) {
        val viewIntent = Intent(Intent.ACTION_VIEW)
        viewIntent.data = Uri.parse(resources.getString(website))
        startActivity(viewIntent)
    }

}