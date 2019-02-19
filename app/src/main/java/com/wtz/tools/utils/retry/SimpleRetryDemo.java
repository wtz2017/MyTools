package com.wtz.tools.utils.retry;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

public class SimpleRetryDemo {

    private long mRetryNumber;
    private ExponentialWaitStrategy mWaitStrategy;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void test() {
        doSomething(new ResultListener() {
            @Override
            public void onResult(boolean success, String info) {
                if (!success) {
                    reDoSomething();
                }
            }
        });
    }

    interface ResultListener {
        void onResult(boolean success, String info);
    }

    private void doSomething(ResultListener listener) {
        System.out.println("start doSomething...");
        if (listener != null) {
            listener.onResult(false, "fail");
        }
    }

    private void reDoSomething() {
        mRetryNumber++;
        if (mWaitStrategy == null) {
            mWaitStrategy = new ExponentialWaitStrategy(30000, 30, TimeUnit.MINUTES);
        }
        mHandler.removeCallbacks(mReDoRunnable);
        mHandler.postDelayed(mReDoRunnable, mWaitStrategy.computeSleepTime(mRetryNumber));
    }

    private Runnable mReDoRunnable = new Runnable() {
        @Override
        public void run() {
            doSomething(new ResultListener() {
                @Override
                public void onResult(boolean success, String info) {
                    if (!success) {
                        reDoSomething();
                    }
                }
            });
        }
    };

    static final class ExponentialWaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        public ExponentialWaitStrategy(long multiplier, long maximumWait, TimeUnit maximumTimeUnit) {
            this.multiplier = multiplier;
            this.maximumWait = maximumTimeUnit.toMillis(maximumWait);
        }

        public long computeSleepTime(long retryNumber) {
            double exp = Math.pow(2, retryNumber);
            long result = Math.round(multiplier * exp);
            if (result > maximumWait) {
                result = maximumWait;
            }
            return result >= 0L ? result : 0L;
        }
    }

}
