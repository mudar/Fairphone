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
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import ca.mudar.fairphone.peaceofmind.Const
import ca.mudar.fairphone.peaceofmind.R
import kotlinx.android.synthetic.main.activity_eula.*


class EulaActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, EulaActivity::class.java)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_eula)

        loadWebView(webview)
    }


    private fun loadWebView(webView: WebView) {
        // Set basic style
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.app_background))
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        // Open links in external browser
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }
        }

        // Load HTML content from assets
        webView.loadUrl(Const.ASSETS_URI + Const.LocalAssets.LICENSE)
    }
}