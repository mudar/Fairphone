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

package ca.mudar.fairphone.peaceofmind;

import android.os.Build;
import android.text.format.DateUtils;

public class Const {
    public static final int MAX_TIME_DEFAULT = 3; // Defaults to 3 hours
    public static final int MINUTE = 60 * 1000;
    public static final int HOUR = 60 * MINUTE;
    public static final long ALARM_INACCURACY = DateUtils.SECOND_IN_MILLIS * 20;
    public static final float INITIAL_PERCENTAGE = 0.1f;
    // TransitionDrawable, in milliseconds
    public static final int TRANSITION_DURATION_FAST = 500;
    public static final int TRANSITION_DURATION_SLOW = 1500;
    // Video flickering delay
    public static final int VIDEO_FLICKER_DURATION = 30;
    public static final String BROADCAST_DURATION_PEACE_OF_MIND = "BROADCAST_DURATION_PEACE_OF_MIND";
    public static boolean SUPPORTS_JELLY_BEAN_MR1 = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    public interface ProgressViewDimensions {
        // Dimensions defined here instead of XML for setLayoutParams() when setting height dynamically
        final static float MIN_HEIGHT_DP = 42f;
        final static float WIDTH_DP = 58.5f;
        final static float MARGIN_LEFT_DP = 13.5f;
        final static float MARGIN_BOTTOM_DP = 12.5f;
    }

    public interface PeaceOfMindIntents {
        public static final String EXTRA_TOGGLE = "pom_toggle";
        public static final String EXTRA_STATE = "pom_state";
//        public static String PIECE_OF_MIND_APP = "ca.mudar.fairphone.peaceofmind.launchapp";
//        public static String STOP_PIECE_OF_MIND = "ca.mudar.fairphone.peaceofmind.stoppeaceofmind";
    }


    public interface PeaceOfMindActions {
        public static final String RINGER_MODE_CHANGED = "ca.mudar.fairphone.peaceofmind.RINGER_MODE_CHANGED";
        public static final String END_PEACE_OF_MIND = "ca.mudar.fairphone.peaceofmind.END_PEACE_OF_MIND";
        public static final String UPDATE_PEACE_OF_MIND = "ca.mudar.fairphone.peaceofmind.UPDATE_PEACE_OF_MIND";
        public static final String TIMER_TICK = "ca.mudar.fairphone.peaceofmind.TIMER_TICK";
    }
}
