package com.wtz.tools.utils.retry;


import android.util.Log;

import com.wtz.tools.utils.judgments.Predicates;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

public class RetryStrategyManager {

    private final String TAG = "RetryStrategyManager";

    private volatile static RetryStrategyManager instance;

    private RetryStrategyManager() {
    }

    public static RetryStrategyManager getInstance() {
        if (instance == null) {
            synchronized (RetryStrategyManager.class) {
                if (instance == null)
                    instance = new RetryStrategyManager();
            }
        }
        return instance;
    }

    /**
     * 获取默认的重试（包括同步和异步）构造器，针对初始化必要环节，会一直重试直到成功
     * 注意：由于异步重试是使用递归实现，使用异步重试时，如果一直重试，重试间隔不能设置太短，以免很快导致栈溢出
     *
     * @param tag 要重试的任务名称，用在调试打印时区分任务信息
     * @return 重试构造器
     */
    public RetryerBuilder<Boolean> getDefaultRetryerBuilder(String tag) {
        return RetryerBuilder.<Boolean>newBuilder()
                // 抛出runtime异常、checked异常时都会重试，但是抛出error不会重试
                .retryIfException()
                // 结果返回false也需要重试
                .retryIfResult(Predicates.equalTo(false))
                // 设置重试之间等待延时策略
//                .withWaitStrategy(WaitStrategies.noWait())
//                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
//                .withWaitStrategy(WaitStrategies.randomWait(5, TimeUnit.SECONDS))
//                .withWaitStrategy(WaitStrategies.randomWait(2, TimeUnit.SECONDS, 5, TimeUnit.SECONDS))
//                .withWaitStrategy(WaitStrategies.incrementingWait(3, TimeUnit.SECONDS, 2, TimeUnit.SECONDS))
//                // 30*1000*(斐波拉契数列：1, 1, 2, 3, 5, 8, 13, 21, 34, 55...),最大30分钟
//                .withWaitStrategy(WaitStrategies.fibonacciWait(30000, 30, TimeUnit.MINUTES))
                // 15*1000*(指数序列：2, 4, 8, 16, 32, 64, 128...),最大30分钟
                .withWaitStrategy(WaitStrategies.exponentialWait(15000, 30, TimeUnit.MINUTES))
                // 设置延时实现的阻塞策略
                .withBlockStrategy(BlockStrategies.threadSleepStrategy())
//                // 设置每次重试超时时间，不支持异步重试调用
//                .withAttemptTimeLimiter(AttemptTimeLimiters.<Boolean>fixedTimeLimit(3, TimeUnit.SECONDS))
                // 设置重试结束策略
//                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .withStopStrategy(StopStrategies.neverStop())
                // 设置每次重试结果的监听
                .withRetryListener(new DefaultRetryListener(tag));
    }

    /**
     * 获取SocketServer的重试（包括同步和异步）构造器
     * 注意：由于异步重试是使用递归实现，使用异步重试时，如果一直重试，重试间隔不能设置太短，以免很快导致栈溢出
     *
     */
    public RetryerBuilder<Object> getSomeObjectRetryerBuilder() {
        return RetryerBuilder.<Object>newBuilder()
                .retryIfException()
                .retryIfResult(Predicates.<Object>isNull())
                .withWaitStrategy(WaitStrategies.exponentialWait(15000, 30, TimeUnit.MINUTES))
                .withBlockStrategy(BlockStrategies.threadSleepStrategy())
                .withStopStrategy(StopStrategies.neverStop())
                .withRetryListener(new DefaultRetryListener("SocketServer"));
    }

    class DefaultRetryListener implements RetryListener {

        private String tag;

        public DefaultRetryListener(String tag) {
            this.tag = tag;
        }

        @Override
        public <V> void onRetry(Attempt<V> attempt) {
            StringBuilder sb = new StringBuilder(tag);
            // 第几次重试，第一次重试其实是第一次调用
            long num = attempt.getAttemptNumber();
            if (num == 1) {
                sb.append("[First invoke]");
            } else {
                sb.append("[Retry ");
                sb.append(num - 1);
                sb.append("]");
                // 距离第一次调用的延迟
                sb.append("DelaySinceFirst=");
                sb.append(attempt.getDelaySinceFirstAttempt() / 1000f);
                sb.append("s;");
            }

            // 重试结果: 是异常终止, 还是正常返回
            if (attempt.hasException()) {
                // 是什么原因导致异常
                sb.append("causeBy=");
                sb.append(getErroInfoFromException(attempt.getExceptionCause()));
            } else {
                // 正常返回时的结果
                sb.append("result=");
                sb.append(attempt.getResult());
            }

            Log.d(TAG, sb.toString());
        }

    }

    private String getErroInfoFromException(Throwable e) {
        if (e == null) {
            return "";
        }

        try {
            Writer stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return e.toString();
    }

}
