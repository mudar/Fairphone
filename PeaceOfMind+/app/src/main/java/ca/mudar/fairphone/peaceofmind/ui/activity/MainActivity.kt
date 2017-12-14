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

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.ui.activity.base.BaseActivity
import ca.mudar.fairphone.peaceofmind.ui.dialog.HelpDialogFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    val tag = "PeaceOfMindActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btn_play.setOnClickListener({
            playAnim()
        })

        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun playAnim() {
        val anim = bg_anim.drawable as Animatable
        anim.start()
    }

    private fun showHelpBottomSheet() {
        val bottomSheet = HelpDialogFragment.newInstance()

        bottomSheet.show(supportFragmentManager, Const.FragmentTags.HELP)
    }
}
