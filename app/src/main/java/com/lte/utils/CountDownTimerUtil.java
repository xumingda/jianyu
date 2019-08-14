/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.lte.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 * <p/>
 * Example of showing a 30 second countdown in a text field:
 * <p/>
 * <pre class="prettyprint">
 * new CountDownTimer(30000, 1000) {
 * <p/>
 * public void onTick(long millisUntilFinished) {
 * mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
 * }
 * <p/>
 * public void onFinish() {
 * mTextField.setText("done!");
 * }
 * }.start();
 * </pre>
 * <p/>
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete.  This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */
public abstract class CountDownTimerUtil {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;
    private static final byte[] lock = new byte[0];

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;
    private boolean isFinish;
    private boolean isStarted;
    private MHandler mHandler = new MHandler(this);

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtil(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public void cancel() {
        synchronized (lock) {
            mCancelled = true;
            isFinish = false;
            isStarted = false;
            mHandler.removeMessages(MSG);
        }

    }

    /**
     * Start the countdown.
     */
    public CountDownTimerUtil start() {
        synchronized (lock) {
            mCancelled = false;
            isStarted = true;
            isFinish = false;
            if (mMillisInFuture <= 0) {
                isFinish = true;
                isStarted = false;
                onFinish();
                return this;
            }
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
            return this;
        }

    }

    public boolean isFinnished() {
        return isFinish;
    }


    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;


    // handles counting down
    private static class MHandler extends Handler {

        private final WeakReference<CountDownTimerUtil> reference;

        public MHandler(CountDownTimerUtil util) {
            reference = new WeakReference<>(util);
        }

        @Override
        public void handleMessage(Message msg) {

            synchronized (lock) {
                if (reference.get().mCancelled) {
                    return;
                }

                final long millisLeft = reference.get().mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    reference.get().isFinish = true;
                    reference.get().isStarted = false;
                    reference.get().onFinish();
                } else if (millisLeft < reference.get().mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    reference.get().onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + reference.get().mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) delay += reference.get().mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    }

    ;
}
