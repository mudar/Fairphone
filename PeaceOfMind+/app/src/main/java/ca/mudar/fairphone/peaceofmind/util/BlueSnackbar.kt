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

package ca.mudar.fairphone.peaceofmind.util

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.model.SnackbarDuration


object BlueSnackbar {
    fun make(view: View, @StringRes resId: Int, @SnackbarDuration duration: Int): Snackbar {
        val snackbar = Snackbar.make(view, view.resources.getText(resId), duration)
        snackbar.view.setBackgroundColor(ContextCompat
                .getColor(view.context, R.color.snackbar_background))

        return snackbar
    }
}