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

public class Const {
    public static final long MAX_TIME = 12 * 60 * 60 * 1000; // Twelve hours
    public static final int MINUTE = 60 * 1000;
    public static final int HOUR = 60 * MINUTE;
    public static final float INITIAL_PERCENTAGE = 0.1f;

    // TransitionDrawable, in milliseconds
    public static final int TRANSITION_DURATION_FAST = 500;
    public static final int TRANSITION_DURATION_SLOW = 1500;
    // Video flickering delay
    public static final int VIDEO_FLICKER_DURATION = 30;

    public interface ProgressViewDimensions {
        // Dimensions defined here instead of XML for setLayoutParams() when setting height dynamically
        final static float MIN_HEIGHT_DP = 42f;
        final static float WIDTH_DP = 58.5f;
        final static float MARGIN_LEFT_DP = 11.5f;
        final static float MARGIN_BOTTOM_DP = 10.5f;
    }

    public interface PeaceOfMindIntents {
        public static final String EXTRA_TOGGLE = "pom_toggle";
        public static final String EXTRA_STATE = "pom_state";
//        public static String PIECE_OF_MIND_APP = "ca.mudar.fairphone.peaceofmind.launchapp";
//        public static String STOP_PIECE_OF_MIND = "ca.mudar.fairphone.peaceofmind.stoppeaceofmind";
    }
}
