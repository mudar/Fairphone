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

package ca.mudar.fairphone.peaceofmind.ui.dialog

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.ContextWrapper
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.mudar.fairphone.peaceofmind.R
import ca.mudar.fairphone.peaceofmind.databinding.DialogDndModesBinding
import ca.mudar.fairphone.peaceofmind.util.CompatHelper
import ca.mudar.fairphone.peaceofmind.viewmodel.AtPeaceViewModel

class DndModesDialogFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(): DndModesDialogFragment {
            return DndModesDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, R.style.AppTheme_DndModesDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogDndModesBinding.inflate(inflater, container, false)

        val parentActivity = activity
        parentActivity?.let {
            binding.viewModel = ViewModelProviders.of(parentActivity).get(AtPeaceViewModel::class.java)
            binding.peaceOfMindController = CompatHelper
                    .getPeaceOfMindController(ContextWrapper(parentActivity))
        }

        return binding.root
    }
}