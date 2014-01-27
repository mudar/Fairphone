/*
 * Copyright (C) 2013 Fairphone Project
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

package ca.mudar.fairphone.peaceofmind.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.mudar.fairphone.peaceofmind.data.PeaceOfMindPrefs;

public class DeviceControllerImplementation {

    public static IDeviceController getDeviceController(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (PeaceOfMindPrefs.hasAirplaneMode(prefs)) {
            return new AirplaneModeDeviceController(context);
        } else {
            return new SilentModeDeviceController(context);
        }
    }

    /**
     * Get the other DeviceController: SilentMode when in AirplaneMode (or the opposite).
     * Used to turn PoM off when toggling the Mode in Settings.
     *
     * @param context
     * @return
     */
    public static IDeviceController getInverseDeviceController(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (PeaceOfMindPrefs.hasAirplaneMode(prefs)) {
            return new SilentModeDeviceController(context);
        } else {
            return new AirplaneModeDeviceController(context);
        }
    }
}
