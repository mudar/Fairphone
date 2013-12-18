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

/*
Modifications (MN 2013-12-16):
- Removed Log outputs
- Commented-out unused variables
- Added SuperuserHelper.requestAccess() to onCreate()
- Moved unRegisterForPeaceOfMindBroadCasts() from onPause() to onDestroy()
- Verify not null for mHelpButton and mCloseButton
- Handle listener leaks using mHasRegisterdReceiver
- Dynamic height for the VerticalSeekBar
- Dynamic measures in updateBarScroll()
- Removed Help
*/
package ca.mudar.fairphone.peaceofmind;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ca.mudar.fairphone.peaceofmind.data.PeaceOfMindStats;
import ca.mudar.fairphone.peaceofmind.superuser.SuperuserHelper;
import ca.mudar.fairphone.peaceofmind.ui.VerticalScrollListener;
import ca.mudar.fairphone.peaceofmind.ui.VerticalSeekBar;

public class PeaceOfMindActivity extends Activity implements
        VerticalScrollListener,
        PeaceOfMindApplicationBroadcastReceiver.Listener,
        OnPreparedListener,
        OnCompletionListener {
    public static final String BROADCAST_TARGET_PEACE_OF_MIND = "BROADCAST_TARGET_PEACE_OF_MIND";
    // public static String START_PEACE_OF_MIND = "START_PEACE_OF_MIND";
    // public static String END_PEACE_OF_MIND = "END_PEACE_OF_MIND";
    public static final String UPDATE_PEACE_OF_MIND = "UPDATE_PEACE_OF_MIND";
    protected static final String TAG = PeaceOfMindActivity.class.getSimpleName();
    private static final String TIMER_TICK = "TIMER_TICK";
    private static final int MINUTE = 60 * 1000;
    private static final int HOUR = 60 * MINUTE;
    private static final float INITIAL_PERCENTAGE = 0.1f;
    // public static int count = 0;
    private static final Semaphore mSemaphore = new Semaphore(1);
    private boolean mHasRegisterdReceiver = false;
    private TextView mTotalTimeText;
    private LinearLayout mCurrentTimeGroup;
    private TextView mCurrentTimeText;
    private LinearLayout mCurrentToTimeGroup;
    private TextView mCurrentToTimeText;
    private TextView mCurrentToText;
    private TextView mCurrentTimeAtText;
    private TextView mCurrentTimePEACEText;
    private LinearLayout mCurrentTimeInPeaceText;
    private VerticalSeekBar mVerticalSeekBar;
    private View mProgressView;
    private View mSeekbarBackgroundOff;
    private View mSeekbarBackgroundOn;
    private VideoView mVideo;
    private PeaceOfMindApplicationBroadcastReceiver mBroadCastReceiver;
    private SharedPreferences mSharedPreferences;
    private ProgressViewParams mProgressViewParams;
    private int mSeekBarHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuperuserHelper.requestAccess(getApplicationContext());

        setupLayout();

        registerForPeaceOfMindBroadCasts();

        setupBroadCastReceiverAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.peaceofmind_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadAvailableData() {
        PeaceOfMindStats currentStats = PeaceOfMindStats.getStatsFromSharedPreferences(mSharedPreferences);

        mVerticalSeekBar.setThumb(getResources().getDrawable(currentStats.mIsOnPeaceOfMind ? R.drawable.seekbar_thumb_on : R.drawable.seekbar_thumb_off));
        mVerticalSeekBar.setThumbOffset(0);
        if (currentStats.mIsOnPeaceOfMind) {
            float targetTimePercent = (float) currentStats.mCurrentRun.mTargetTime / (float) PeaceOfMindStats.MAX_TIME;

            mVerticalSeekBar.setInvertedProgress((int) (targetTimePercent * mVerticalSeekBar.getHeight()));

            updateTextForNewTime(currentStats.mCurrentRun.mPastTime, currentStats.mCurrentRun.mTargetTime);
            updateTimeTextLabel(targetTimePercent * 100);
            updateScreenTexts();
        } else {
            mTotalTimeText.setText(generateStringTimeFromMillis(0, true));
            mCurrentTimeText.setText(generateStringTimeFromMillis(0, true));
        }

        updateBackground(currentStats.mIsOnPeaceOfMind);
        mVideo.setBackgroundResource(currentStats.mIsOnPeaceOfMind ? R.drawable.background_on : R.drawable.background_off);
    }

    private void updateScreenTexts() {
        PeaceOfMindStats currentStats = PeaceOfMindStats.getStatsFromSharedPreferences(mSharedPreferences);

        int blue = getResources().getColor(R.color.blue);
        int grey = getResources().getColor(R.color.blue_grey);

        if (currentStats.mIsOnPeaceOfMind) {
            // current time is blue
            mCurrentTimeText.setTextColor(blue);
            mCurrentTimeAtText.setTextColor(blue);
            mCurrentTimePEACEText.setTextColor(blue);

            mCurrentTimeText.setAlpha(1.0f);
            mCurrentTimeAtText.setAlpha(1.0f);
            mCurrentTimePEACEText.setAlpha(1.0f);

            mCurrentToTimeGroup.setVisibility(View.VISIBLE);

            mSeekbarBackgroundOff.setVisibility(View.GONE);
            mSeekbarBackgroundOn.setVisibility(View.VISIBLE);
        } else {
            // show the current time and text at grey
            mCurrentTimeText.setTextColor(grey);
            mCurrentTimeAtText.setTextColor(grey);
            mCurrentTimePEACEText.setTextColor(grey);

            mCurrentTimeText.setAlpha(0.5f);
            mCurrentTimeAtText.setAlpha(0.5f);
            mCurrentTimePEACEText.setAlpha(0.5f);

            mCurrentToTimeGroup.setVisibility(View.INVISIBLE);

            mSeekbarBackgroundOff.setVisibility(View.VISIBLE);
            mSeekbarBackgroundOn.setVisibility(View.GONE);
        }

        if (mTotalTimeText.getVisibility() == View.VISIBLE) {
            //hide the current time group when the target time approaches
            //TODO: Fix the ugly magical numbers
            float position = mCurrentTimeGroup.getY() - mTotalTimeText.getY();
            float alpha = (position < 500) ? (10.0f * (position - 50) / 100.0f) : 1.0f;
            mCurrentTimeGroup.setAlpha(alpha);
            mCurrentTimeInPeaceText.setAlpha(alpha);
        } else {
            if (mCurrentTimeGroup.getAlpha() != 1.0f) {
                if (!currentStats.mIsOnPeaceOfMind) {
                    Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.target_time_fade_in_fast);
                    mCurrentTimeGroup.startAnimation(fadeIn);
                    mCurrentTimeInPeaceText.startAnimation(fadeIn);
                }
                mCurrentTimeGroup.setAlpha(1.0f);
                mCurrentTimeInPeaceText.setAlpha(1.0f);
            }
        }
    }

    private void updateBackground(boolean on) {
//        View backgroundOverlay = findViewById(R.id.backgroundOverlay);
//
//        int backgroundDrawableId = on ? R.drawable.background_on : R.drawable.background_off;
//
//        // setup the background
//        backgroundOverlay.setBackgroundResource(backgroundDrawableId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideo.setVisibility(View.INVISIBLE);
        mVideo.stopPlayback();

        // load data from the shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        updateScreenTexts();
    }

    @Override
    protected void onDestroy() {
        unRegisterForPeaceOfMindBroadCasts();

        super.onDestroy();
    }

    private void setupLayout() {
        // Get dimensions using DisplayMetrics
        mSeekBarHeight = -1;
        mProgressViewParams = new ProgressViewParams(getResources().getDisplayMetrics());

        mTotalTimeText = (TextView) findViewById(R.id.timeTextTotal);

        mCurrentTimeGroup = (LinearLayout) findViewById(R.id.timeTextCurrentGroup);
        mCurrentTimeText = (TextView) findViewById(R.id.timeTextCurrent);
        mCurrentToTimeGroup = (LinearLayout) findViewById(R.id.toTimeGroup);
        mCurrentToTimeText = (TextView) findViewById(R.id.toTimeText);
        mCurrentToText = (TextView) findViewById(R.id.toText);

        mCurrentTimeInPeaceText = (LinearLayout) findViewById(R.id.inPeaceTextCurrent);

        mCurrentTimeAtText = (TextView) findViewById(R.id.currentAtText);
        mCurrentTimePEACEText = (TextView) findViewById(R.id.currentPeaceText);

        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);

        mProgressView = findViewById(R.id.progressView);
        mSeekbarBackgroundOff = findViewById(R.id.seekbar_background_off);
        mSeekbarBackgroundOn = findViewById(R.id.seekbar_background_on);

        if (mVerticalSeekBar != null) {
            mVerticalSeekBar.setPeaceListener(this);
        }

        mVideo = (VideoView) findViewById(R.id.pomVideo);

        mVideo.setVisibility(View.INVISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fp_start_pom_video);

        mVideo.setMediaController(null);
        mVideo.requestFocus();
        mVideo.setVideoURI(uri);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loadAvailableData();

        if (mSeekBarHeight == -1) {
            mSeekBarHeight = mVerticalSeekBar.getMeasuredHeight();
        }
    }

    private void registerForPeaceOfMindBroadCasts() {
        if (!mHasRegisterdReceiver) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(PeaceOfMindApplicationBroadcastReceiver.PEACE_OF_MIND_STARTED);
            filter.addAction(PeaceOfMindApplicationBroadcastReceiver.PEACE_OF_MIND_UPDATED);
            filter.addAction(PeaceOfMindApplicationBroadcastReceiver.PEACE_OF_MIND_ENDED);
            filter.addAction(PeaceOfMindApplicationBroadcastReceiver.PEACE_OF_MIND_TICK);

            mBroadCastReceiver = new PeaceOfMindApplicationBroadcastReceiver(this);
            registerReceiver(mBroadCastReceiver, filter);
            mHasRegisterdReceiver = true;
        }
    }

    private void unRegisterForPeaceOfMindBroadCasts() {
        if (mHasRegisterdReceiver) {
            unregisterReceiver(mBroadCastReceiver);
            mHasRegisterdReceiver = false;
        }
    }

    private void setupBroadCastReceiverAlarm() {
        Intent alarmIntent = new Intent(this, PeaceOfMindBroadCastReceiver.class);
        alarmIntent.setAction(PeaceOfMindActivity.TIMER_TICK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), MINUTE, pendingIntent);
    }

    @Override
    public void updateBarScroll(float progress) {

        final int pos = (int) ((100 - progress) * mSeekBarHeight * 0.5 / 100);
        mTotalTimeText.setY(pos);

        updateTimeTextLabel(progress);

        if (mTotalTimeText.getVisibility() == View.INVISIBLE) {
            mTotalTimeText.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.target_time_fade_in_fast);
            mTotalTimeText.startAnimation(fadeIn);
        }

        updateScreenTexts();
    }

    @Override
    public synchronized void scrollEnded(float percentage) {
        if (mTotalTimeText.getVisibility() == View.VISIBLE) {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast);
            mTotalTimeText.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTotalTimeText.setVisibility(View.INVISIBLE);
                    updateScreenTexts();
                }
            });
        }

        long targetTime = roundToInterval((long) (percentage * PeaceOfMindStats.MAX_TIME));

        Intent intent = new Intent(getApplicationContext(), PeaceOfMindBroadCastReceiver.class);
        intent.setAction(PeaceOfMindActivity.UPDATE_PEACE_OF_MIND);

        intent.putExtra(PeaceOfMindActivity.BROADCAST_TARGET_PEACE_OF_MIND, targetTime);

        sendBroadcast(intent);
    }

    private long roundToInterval(long time) {

        int hours = (int) (time / HOUR);
        int minutes = (int) ((time - hours * HOUR) / MINUTE);

        int index = minutes % 10;

        long newTime = 0;

        switch (index) {
            case 1:
            case 6:
                newTime -= MINUTE;
                break;
            case 2:
            case 7:
                newTime -= 2 * MINUTE;
                break;
            case 3:
            case 8:
                newTime += 2 * MINUTE;
                break;
            case 4:
            case 9:
                newTime += MINUTE;
                break;
        }

//        Log.d(TAG, "Index: " + index + " - " + newTime);

        return time + newTime;
    }

    private void updateTextForNewTime(long timePast, long targetTime) {
        long maxTime = PeaceOfMindStats.MAX_TIME;

        float timePercentage = 0;

        long timeUntilTarget = targetTime - timePast;
        mCurrentTimeText.setText(generateStringTimeFromMillis(timeUntilTarget, timeUntilTarget <= 0));

        int finalY = getCurrentProgressY(timePast, targetTime, maxTime, timePercentage);

        finalY = Math.max(finalY, mProgressViewParams.minHeight);   // Avoid clipping at the layout bottom

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mProgressViewParams.width, finalY);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.verticalSeekBar);
        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.verticalSeekBar);
        params.setMargins(mProgressViewParams.marginLeft, 0, 0, mProgressViewParams.marginBottom);
        mProgressView.setLayoutParams(params);

        float pos = mSeekBarHeight - finalY - 12;
        mCurrentTimeGroup.setY(pos);
        mCurrentTimeInPeaceText.setY(pos);
    }

    private int getCurrentProgressY(long timePast, long targetTime, long maxTime, float timePercentage) {
        if (targetTime > 0) {
            timePercentage = (((float) timePast / (float) (maxTime)));
        }

//        Log.d(TAG, "Updating time to " + timePercentage + " - " + timePast + " target time " + targetTime);

        return (int) (0.8f * mVerticalSeekBar.getHeight() * timePercentage + (mVerticalSeekBar.getHeight() * INITIAL_PERCENTAGE));
    }

    private String generateStringTimeFromMillis(long timePast, boolean reset) {
        int hours = 0;
        int minutes = 0;
        if (!reset) {
            hours = (int) (timePast / HOUR);
            int timeInMinutes = (int) (timePast - hours * HOUR);

            if (hours == 0) {
                minutes = timeInMinutes - MINUTE > 0 ? timeInMinutes / MINUTE : 1;
            } else {
                minutes = timeInMinutes / MINUTE;
            }
        }

        String timeStr = String.format("%d%s%02d", hours, getResources().getString(R.string.hour_separator), minutes);
        if (hours == 0) {
            mCurrentToText.setText(getResources().getString(R.string.to_m));
        } else {
            mCurrentToText.setText(getResources().getString(R.string.to_h));
        }

        return timeStr;
    }

    private void updateTimeTextLabel(float progress) {
        long targetTime = roundToInterval((long) (PeaceOfMindStats.MAX_TIME * progress / 100.0f));

        mTotalTimeText.setText(generateStringTimeFromMillis(targetTime, targetTime == 0));
        mCurrentToTimeText.setText(generateStringTimeFromMillis(targetTime, targetTime == 0));
    }

    @Override
    public void peaceOfMindTick(long pastTime, long targetTime) {
        updateTextForNewTime(pastTime, targetTime);
        updateScreenTexts();
    }

    @Override
    public synchronized void peaceOfMindStarted(long targetTime) {
        try {
            mSemaphore.tryAcquire(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast);
        mSeekbarBackgroundOff.startAnimation(fadeOut);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast);

        mSeekbarBackgroundOn.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSeekbarBackgroundOff.setVisibility(View.GONE);
                mSeekbarBackgroundOn.setVisibility(View.VISIBLE);

                mSemaphore.release();
            }
        });

        mVerticalSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_on));
        mVerticalSeekBar.setThumbOffset(0);

        // fix thumb position
        float targetTimePercent = (float) targetTime / (float) PeaceOfMindStats.MAX_TIME;

        updateTextForNewTime(0, targetTime);
        mVerticalSeekBar.setInvertedProgress((int) (targetTimePercent * mVerticalSeekBar.getHeight()));

        startPeaceOfMindVideo();
    }

    private void startPeaceOfMindVideo() {
        mVideo.setBackgroundResource(R.drawable.background_off);
        mVideo.setVisibility(View.VISIBLE);

        mVideo.setOnPreparedListener(this);
        mVideo.setOnCompletionListener(this);
        mVideo.setDrawingCacheEnabled(true);
    }

    private void stopPeaceOfMindVideo() {
        mVideo.removeCallbacks(null);
        if (mVideo.getVisibility() != View.VISIBLE) {
            mVideo.setVisibility(View.VISIBLE);
        }

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast);
        mVideo.startAnimation(fadeOut);

        updateBackground(false);
        fadeOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mVideo.setVisibility(View.INVISIBLE);
                mVideo.stopPlayback();
            }
        });
    }

    public void onPrepared(MediaPlayer mp) {
        //Used to avoid the initial black flicker
        //remove the foreground 30 miliseconds after the video starts
        mVideo.postDelayed(new Runnable() {
            public void run() {
                if (mVideo.isPlaying()) {
//                    mVideo.setBackgroundResource(0);
                }

            }
        }, 30);

        //Used to avoid the final black flicker
        //remove the foreground 20 miliseconds before the video ends
        mVideo.postDelayed(new Runnable() {
            public void run() {
                if (mVideo.isPlaying()) {
//                    mVideo.setBackgroundResource(R.drawable.background_on);
                }

            }
        }, mVideo.getDuration() - 20);
        mVideo.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        updateBackground(true);
        mVideo.removeCallbacks(null);
//        mVideo.setBackgroundResource(R.drawable.background_on);
        mVideo.stopPlayback();
    }

    @Override
    public synchronized void peaceOfMindEnded() {
        try {
            mSemaphore.tryAcquire(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast);

        mSeekbarBackgroundOff.startAnimation(fadeIn);

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast);
        mSeekbarBackgroundOn.startAnimation(fadeOut);

        fadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSeekbarBackgroundOff.setVisibility(View.VISIBLE);
                mSeekbarBackgroundOn.setVisibility(View.GONE);

                updateScreenTexts();
                stopPeaceOfMindVideo();

                mSemaphore.release();
            }
        });

        updateScreenTexts();
        mVerticalSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_off));
        mVerticalSeekBar.setThumbOffset(0);
        mVerticalSeekBar.setInvertedProgress(0);
        updateTextForNewTime(0, 0);
        updateTimeTextLabel(0);

        mTotalTimeText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void peaceOfMindUpdated(long pastTime, long newTargetTime) {
        updateTextForNewTime(pastTime, newTargetTime);
    }

    private class ProgressViewParams {

        final private static float MIN_HEIGHT_DP = 42f;
        final private static float WIDTH_DP = 58f;
        final private static float MARGIN_LEFT_DP = 13.5f;
        final private static float MARGIN_BOTTOM_DP = 10.5f;
        final public int minHeight;
        final public int width;
        final public int marginLeft;
        final public int marginBottom;

        private ProgressViewParams(DisplayMetrics metrics) {
            // Dimensions holder, to reduce dimension measurements
            minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_HEIGHT_DP, metrics);
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DP, metrics);
            marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MARGIN_LEFT_DP, metrics);
            marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MARGIN_BOTTOM_DP, metrics);
        }
    }
}

