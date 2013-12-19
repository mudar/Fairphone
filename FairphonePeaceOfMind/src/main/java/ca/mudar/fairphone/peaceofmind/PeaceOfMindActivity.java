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
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
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

import ca.mudar.fairphone.peaceofmind.data.PeaceOfMindStats;
import ca.mudar.fairphone.peaceofmind.superuser.SuperuserHelper;
import ca.mudar.fairphone.peaceofmind.ui.VerticalScrollListener;
import ca.mudar.fairphone.peaceofmind.ui.VerticalSeekBar;
import ca.mudar.fairphone.peaceofmind.utils.TimeHelper;

public class PeaceOfMindActivity extends Activity implements
        VerticalScrollListener,
        PeaceOfMindApplicationBroadcastReceiver.Listener,
        OnPreparedListener,
        OnCompletionListener {
    public static final String BROADCAST_TARGET_PEACE_OF_MIND = "BROADCAST_TARGET_PEACE_OF_MIND";
    public static final String UPDATE_PEACE_OF_MIND = "UPDATE_PEACE_OF_MIND";
    protected static final String TAG = PeaceOfMindActivity.class.getSimpleName();
    private static final String TIMER_TICK = "TIMER_TICK";
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
    private View mSeekbarBackground;
    private TransitionDrawable mSeekbarBackgroundTransition;
    private VideoView mVideo;
    private View mBackgroundOverlay;
    private PeaceOfMindApplicationBroadcastReceiver mBroadCastReceiver;
    private SharedPreferences mSharedPreferences;
    private ProgressViewParams mProgressViewParams;
    private int mSeekBarHeight = -1;
    private Resources mResources;

    private static void animateBackground(View view, int Duration) {
        TransitionDrawable background = (TransitionDrawable) view.getBackground();
        background.resetTransition();
        background.setCrossFadeEnabled(true);
        background.startTransition(Duration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuperuserHelper.requestAccess(getApplicationContext());

        mResources = getResources();
        setupLayout();

        registerForPeaceOfMindBroadCasts();

        setupBroadCastReceiverAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideo.setVisibility(View.INVISIBLE);
        mVideo.stopPlayback();

        // load data from the shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        updateScreenTexts();
        updateScreenBackgrounds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.peaceofmind_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        unRegisterForPeaceOfMindBroadCasts();

        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loadAvailableData();

        if (mSeekBarHeight == -1) {
            mSeekBarHeight = mVerticalSeekBar.getMeasuredHeight();
        }
    }

    private void loadAvailableData() {
        PeaceOfMindStats currentStats = PeaceOfMindStats.getStatsFromSharedPreferences(mSharedPreferences);

        mVerticalSeekBar.setThumb(mResources.getDrawable(currentStats.mIsOnPeaceOfMind ? R.drawable.seekbar_thumb_on : R.drawable.seekbar_thumb_off));
        mVerticalSeekBar.setThumbOffset(0);
        if (currentStats.mIsOnPeaceOfMind) {
            float targetTimePercent = (float) currentStats.mCurrentRun.mTargetTime / (float) Const.MAX_TIME;

            mVerticalSeekBar.setInvertedProgress((int) (targetTimePercent * mVerticalSeekBar.getHeight()));

            updateTextForNewTime(currentStats.mCurrentRun.mPastTime, currentStats.mCurrentRun.mTargetTime);
            updateTimeTextLabel(targetTimePercent * 100);
            updateScreenTexts();
        } else {
            final String[] stringTime = TimeHelper.generateStringTimeFromMillis(0, true, mResources);
            mTotalTimeText.setText(stringTime[0]);
            mCurrentTimeText.setText(stringTime[0]);
            mCurrentToText.setText(stringTime[1]);
        }

        updateBackground(currentStats.mIsOnPeaceOfMind);
    }

    private void updateScreenBackgrounds() {
        final PeaceOfMindStats currentStats = PeaceOfMindStats.getStatsFromSharedPreferences(mSharedPreferences);
        if (currentStats.mIsOnPeaceOfMind) {
            mSeekbarBackgroundTransition.startTransition(Const.TRANSITION_DURATION_FAST);
        } else {
            mSeekbarBackgroundTransition.resetTransition();
        }
    }

    private void updateScreenTexts() {
        final PeaceOfMindStats currentStats = PeaceOfMindStats.getStatsFromSharedPreferences(mSharedPreferences);

        // TODO: optimize this!
        int blue = mResources.getColor(R.color.blue);
        int grey = mResources.getColor(R.color.blue_grey);

        if (currentStats.mIsOnPeaceOfMind) {
            // current time is blue
            mCurrentTimeText.setTextColor(blue);
            mCurrentTimeAtText.setTextColor(blue);
            mCurrentTimePEACEText.setTextColor(blue);

            mCurrentTimeText.setAlpha(1.0f);
            mCurrentTimeAtText.setAlpha(1.0f);
            mCurrentTimePEACEText.setAlpha(1.0f);

            mCurrentToTimeGroup.setVisibility(View.VISIBLE);
        } else {
            // show the current time and text at grey
            mCurrentTimeText.setTextColor(grey);
            mCurrentTimeAtText.setTextColor(grey);
            mCurrentTimePEACEText.setTextColor(grey);

            mCurrentTimeText.setAlpha(0.5f);
            mCurrentTimeAtText.setAlpha(0.5f);
            mCurrentTimePEACEText.setAlpha(0.5f);

            mCurrentToTimeGroup.setVisibility(View.INVISIBLE);
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
        mBackgroundOverlay.setBackgroundResource(on ? R.drawable.transition_bg_toggle_off : R.drawable.transition_bg_off_fadeout);
    }

    private void setupLayout() {
        // Get dimensions using DisplayMetrics
        mProgressViewParams = new ProgressViewParams(mResources.getDisplayMetrics());
        mSeekBarHeight = -1;

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
        mSeekbarBackground = findViewById(R.id.seekbar_background);
        mSeekbarBackgroundTransition = (TransitionDrawable) mSeekbarBackground.getBackground();
        mSeekbarBackgroundTransition.setCrossFadeEnabled(true);

        if (mVerticalSeekBar != null) {
            mVerticalSeekBar.setPeaceListener(this);
        }

        mVideo = (VideoView) findViewById(R.id.pomVideo);
        mBackgroundOverlay = findViewById(R.id.background_overlay);

        mVideo.setVisibility(View.INVISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fp_start_pom_video);

        mVideo.setMediaController(null);
        mVideo.requestFocus();
        mVideo.setVideoURI(uri);
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

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), Const.MINUTE, pendingIntent);
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

        long targetTime = TimeHelper.roundToInterval((long) (percentage * Const.MAX_TIME));

        Intent intent = new Intent(getApplicationContext(), PeaceOfMindBroadCastReceiver.class);
        intent.setAction(PeaceOfMindActivity.UPDATE_PEACE_OF_MIND);

        intent.putExtra(PeaceOfMindActivity.BROADCAST_TARGET_PEACE_OF_MIND, targetTime);

        sendBroadcast(intent);
    }

    private void updateTextForNewTime(long timePast, long targetTime) {
        long timeUntilTarget = targetTime - timePast;
        final String[] stringTime = TimeHelper.generateStringTimeFromMillis(timeUntilTarget, timeUntilTarget <= 0, mResources);
        mCurrentTimeText.setText(stringTime[0]);
        mCurrentToText.setText(stringTime[1]);

        int finalY = TimeHelper.getCurrentProgressY(timePast, targetTime, mSeekBarHeight);

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

    @Override
    public void peaceOfMindTick(long pastTime, long targetTime) {
        updateTextForNewTime(pastTime, targetTime);
        updateScreenTexts();
    }

    @Override
    public synchronized void peaceOfMindStarted(long targetTime) {
        mSeekbarBackgroundTransition.startTransition(Const.TRANSITION_DURATION_SLOW);

        mVerticalSeekBar.setThumb(mResources.getDrawable(R.drawable.seekbar_thumb_on));
        mVerticalSeekBar.setThumbOffset(0);

        // fix thumb position
        float targetTimePercent = (float) targetTime / (float) Const.MAX_TIME;

        updateTextForNewTime(0, targetTime);
        updateScreenBackgrounds();
        mVerticalSeekBar.setInvertedProgress((int) (targetTimePercent * mVerticalSeekBar.getHeight()));

        startPeaceOfMindVideo();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        /**
         * Used to avoid the initial black flicker:
         * 30 miliseconds after the video start, we animate transition_bg_off_fadeout
         * which would then display the second item background_translucent
          */
        mVideo.postDelayed(new Runnable() {
            public void run() {
                if (mVideo.isPlaying()) {
                    animateBackground(mBackgroundOverlay, Const.TRANSITION_DURATION_FAST);
                }
            }
        }, Const.VIDEO_FLICKER_DURATION);

        /**
         * Used to avoid the final black flicker
         * Before the video ends, we change the background to transition_bg_on_fadein
         * then the animation transitions from background_translucent (similar as above)
         * to background_on (solid background)
         */
        mVideo.postDelayed(new Runnable() {
            public void run() {
                if (mVideo.isPlaying()) {
                    mBackgroundOverlay.setBackgroundResource(R.drawable.transition_bg_on_fadein);
                    animateBackground(mBackgroundOverlay, Const.TRANSITION_DURATION_FAST);
                }

            }
        }, mVideo.getDuration() - Const.TRANSITION_DURATION_FAST);
        mVideo.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        updateBackground(true);
        stopPeaceOfMindVideo();
    }

    @Override
    public synchronized void peaceOfMindEnded() {
        animateBackground(mBackgroundOverlay, Const.TRANSITION_DURATION_SLOW);

        updateScreenTexts();
        updateScreenBackgrounds();
        mVerticalSeekBar.setThumb(mResources.getDrawable(R.drawable.seekbar_thumb_off));
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

    private void startPeaceOfMindVideo() {
        mBackgroundOverlay.setBackgroundResource(R.drawable.transition_bg_off_fadeout);
        mVideo.setVisibility(View.VISIBLE);

        mVideo.setOnPreparedListener(this);
        mVideo.setOnCompletionListener(this);
        mVideo.setDrawingCacheEnabled(true);
    }

    private void stopPeaceOfMindVideo() {
        mVideo.removeCallbacks(null);
        mVideo.setVisibility(View.INVISIBLE);
        mVideo.stopPlayback();
    }

    private void updateTimeTextLabel(float progress) {
        final long targetTime = TimeHelper.roundToInterval((long) (Const.MAX_TIME * progress / 100.0f));

        final String[] stringTime = TimeHelper.generateStringTimeFromMillis(targetTime, targetTime == 0, mResources);
        mTotalTimeText.setText(stringTime[0]);
        mCurrentToTimeText.setText(stringTime[0]);
        mCurrentToText.setText(stringTime[1]);
    }

    private class ProgressViewParams {
        final public int minHeight;
        final public int width;
        final public int marginLeft;
        final public int marginBottom;

        private ProgressViewParams(DisplayMetrics metrics) {
            // Dimensions holder, to reduce calls to DisplayMetrics
            minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Const.ProgressViewDimensions.MIN_HEIGHT_DP, metrics);
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Const.ProgressViewDimensions.WIDTH_DP, metrics);
            marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Const.ProgressViewDimensions.MARGIN_LEFT_DP, metrics);
            marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Const.ProgressViewDimensions.MARGIN_BOTTOM_DP, metrics);
        }
    }
}

