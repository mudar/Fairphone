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

package ca.mudar.fairphone.peaceofmind.ui;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import ca.mudar.fairphone.peaceofmind.R;

public class AboutDialog extends DialogPreference {
    public AboutDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final TextView uiWebsite = ((TextView) view.findViewById(R.id.about_website));
        final TextView uiVersion = ((TextView) view.findViewById(R.id.about_version));

        final Resources res = getContext().getResources();
        uiVersion.setText(String.format(res.getString(R.string.about_version), res.getString(R.string.app_version)));

        MovementMethod method = LinkMovementMethod.getInstance();
        uiWebsite.setMovementMethod(method);
        ((TextView) view.findViewById(R.id.about_credits_1)).setMovementMethod(method);
        ((TextView) view.findViewById(R.id.about_credits_2)).setMovementMethod(method);
    }
}
