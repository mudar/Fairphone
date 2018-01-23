/*
 * Copyright 2014 Google Inc. All rights reserved.
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

/*
 * Modifications:
 * -Imported from iosched 2015
 * -Changed package name
 */

package ca.mudar.fairphone.peaceofmind.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import static ca.mudar.fairphone.peaceofmind.BuildConfig.LOGGING_LEVEL;
import static ca.mudar.fairphone.peaceofmind.BuildConfig.USE_CRASHLYTICS;


public class LogUtils {
    private static final String LOG_PREFIX = "huegoal_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;


    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGV(final String tag, String message) {
        if (Log.VERBOSE >= LOGGING_LEVEL) {
            Log.v(tag, message);
        }
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
        if (Log.VERBOSE >= LOGGING_LEVEL) {
            Log.v(tag, message, cause);
        }
    }

    public static void LOGD(final String tag, String message) {
        if (Log.DEBUG >= LOGGING_LEVEL) {
            Log.d(tag, message);
        }
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        if (Log.DEBUG >= LOGGING_LEVEL) {
            Log.d(tag, message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (Log.INFO >= LOGGING_LEVEL) {
            Log.i(tag, message);
        }
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        if (Log.INFO >= LOGGING_LEVEL) {
            Log.i(tag, message, cause);
        }
    }

    public static void LOGW(final String tag, String message) {
        if (Log.WARN >= LOGGING_LEVEL) {
            Log.w(tag, message);
        }
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        if (Log.WARN >= LOGGING_LEVEL) {
            Log.w(tag, message, cause);
        }
    }

    public static void LOGE(final String tag, String message) {
        if (Log.ERROR >= LOGGING_LEVEL) {
            Log.e(tag, message);
        }
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        if (Log.ERROR >= LOGGING_LEVEL) {
            Log.e(tag, message, cause);
        }
    }

    public static void REMOTE_LOG(Throwable cause) {
        if (USE_CRASHLYTICS) {
            Crashlytics.logException(cause);
        }
        cause.printStackTrace();
    }

    private LogUtils() {
    }
}
