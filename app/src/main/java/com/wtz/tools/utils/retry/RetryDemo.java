package com.wtz.tools.utils.retry;

import com.wtz.tools.utils.retry.async.AsyncCallResult;
import com.wtz.tools.utils.retry.async.AsyncCallable;

public class RetryDemo {
    private int count = 0;

    public void test() {

        RetryStrategyManager.getInstance().getDefaultRetryerBuilder("RetryDemo").buildAsyncRetryer()
                .call(new AsyncCallable<Boolean>() {
                    @Override
                    public void call(final AsyncCallResult<Boolean> result) throws Exception {
                        count++;
                        if (count < 5) {
                            result.onResult(false);
                        } else {
                            result.onResult(true);
                        }
                    }
                }, new AsyncCallResult<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        if (Boolean.TRUE.equals(result)) {
                            // do something
                        } else {
                            // do something
                        }
                    }

                    @Override
                    public void onException(Throwable t) {
                        // do something
                    }
                });
    }

}
