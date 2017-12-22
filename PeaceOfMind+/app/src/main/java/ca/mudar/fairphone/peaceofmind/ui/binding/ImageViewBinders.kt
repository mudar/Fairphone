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
import android.graphics.drawable.Animatable
import android.support.v7.widget.AppCompatImageView
import ca.mudar.fairphone.peaceofmind.R


@BindingAdapter("isAtPeaceAnim")
fun setImageVectorAnim(imageView: AppCompatImageView, isAtPeace: Boolean?) {
    isAtPeace?.let {
        val isInitial = (imageView.drawable == null)
        // xor operator flips value when `isInitial=true`
        imageView.setImageResource(when (isAtPeace.xor(isInitial)) {
            true -> R.drawable.water_fill_anim
            false -> R.drawable.water_purge_anim
        })
        val anim = imageView.drawable as Animatable
        if (!isInitial) {
            anim.start()
        }
    }
}
