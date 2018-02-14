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

package ca.mudar.fairphone.peaceofmind.ui.binding

import android.databinding.BindingAdapter
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.view.View
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.model.DndModeButton
import ca.mudar.fairphone.peaceofmind.util.CompatHelper

@BindingAdapter("currentAtPeaceMode", "currentOfflineMode", requireAll = true)
fun setDndCurrentMode(button: AppCompatButton,
                      atPeaceMode: Int?,
                      isAtPeaceOfflineMode: Boolean? = false) {
    val context = button.context
            ?: return

    val dndCurrentModeBtn = CompatHelper.getDndCurrentModeButton(atPeaceMode ?: 0,
            isAtPeaceOfflineMode ?: false)

    if (dndCurrentModeBtn != null) {
        button.text = context.getText(dndCurrentModeBtn.label)
        val drawable = VectorDrawableCompat.create(context.resources, dndCurrentModeBtn.iconActive, context.theme)

        button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    } else {
        button.visibility = View.GONE
    }
}

@BindingAdapter("activeAtPeaceMode", "activeOfflineMode", requireAll = false)
fun setDndActiveMode(button: AppCompatButton,
                     atPeaceMode: Int?,
                     isAtPeaceOfflineMode: Boolean? = false) {
    /**
     * Loops through compoundDrawables to replace icon at same index.
     * Buttons can have a start or top drawable
     */
    fun setButtonCompoundDrawable(btn: AppCompatButton, @DrawableRes icon: Int) {
        // First non-null drawable
        val index = btn.compoundDrawables.indexOfFirst { it != null }
        // Initialize empty array
        val compoundDrawables = intArrayOf(0, 0, 0, 0)
        // Replace icon at same index
        compoundDrawables[index] = icon
        // Use array to set all 4 drawables (including 3 null)
        btn.setCompoundDrawablesWithIntrinsicBounds(
                compoundDrawables[0],
                compoundDrawables[1],
                compoundDrawables[2],
                compoundDrawables[3]
        )
    }

    val context = button.context
            ?: return

    val dndCurrentModeBtn = CompatHelper.getDndCurrentModeButton(atPeaceMode ?: 0,
            isAtPeaceOfflineMode ?: false)

    val isActive = button.id == (dndCurrentModeBtn?.id ?: 0)
    when (isActive) {
        true -> {
            button.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            setButtonCompoundDrawable(button, dndCurrentModeBtn!!.iconActive)
        }
        false -> {
            button.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            setButtonCompoundDrawable(button, DndModeButton(button.id).iconIdle)
        }
    }
}
