/*
 * Copyright 2012-2015 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wtz.tools.utils.retry;


import android.support.annotation.NonNull;

import com.wtz.tools.utils.TimeLimiter.SimpleTimeLimiter;
import com.wtz.tools.utils.TimeLimiter.TimeLimiter;
import com.wtz.tools.utils.judgments.Preconditions;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Factory class for instances of {@link AttemptTimeLimiter}
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class AttemptTimeLimiters {

    private AttemptTimeLimiters() {
    }

    /**
     * @param <V> The type of the computation result.
     * @return an {@link AttemptTimeLimiter} impl which has no time limit
     */
    public static <V> AttemptTimeLimiter<V> noTimeLimit() {
        return new NoAttemptTimeLimit<V>();
    }

    /**
     * For control over thread management, it is preferable to offer an {@link ExecutorService} through the other
     * factory method, {@link #fixedTimeLimit(long, TimeUnit, ExecutorService)}. See the note on
     * {@link SimpleTimeLimiter#SimpleTimeLimiter(ExecutorService)}, which this AttemptTimeLimiter uses.
     *
     * @param duration that an attempt may persist before being circumvented
     * @param timeUnit of the 'duration' arg
     * @param <V>      the type of the computation result
     * @return an {@link AttemptTimeLimiter} with a fixed time limit for each attempt
     */
    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, @NonNull TimeUnit timeUnit) {
        Preconditions.checkNotNull(timeUnit);
        return new FixedAttemptTimeLimit<V>(duration, timeUnit);
    }

    /**
     * @param duration        that an attempt may persist before being circumvented
     * @param timeUnit        of the 'duration' arg
     * @param executorService used to enforce time limit
     * @param <V>             the type of the computation result
     * @return an {@link AttemptTimeLimiter} with a fixed time limit for each attempt
     */
    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, @NonNull TimeUnit timeUnit, @NonNull ExecutorService executorService) {
        Preconditions.checkNotNull(timeUnit);
        return new FixedAttemptTimeLimit<V>(duration, timeUnit, executorService);
    }

    private static final class NoAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {
        @Override
        public V call(Callable<V> callable) throws Exception {
            return callable.call();
        }
    }

    private static final class FixedAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {

        private final TimeLimiter timeLimiter;
        private final long duration;
        private final TimeUnit timeUnit;

        public FixedAttemptTimeLimit(long duration, @NonNull TimeUnit timeUnit) {
            this(new SimpleTimeLimiter(), duration, timeUnit);
        }

        public FixedAttemptTimeLimit(long duration, @NonNull TimeUnit timeUnit, @NonNull ExecutorService executorService) {
            this(new SimpleTimeLimiter(executorService), duration, timeUnit);
        }

        private FixedAttemptTimeLimit(@NonNull TimeLimiter timeLimiter, long duration, @NonNull TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeLimiter);
            Preconditions.checkNotNull(timeUnit);
            this.timeLimiter = timeLimiter;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        @Override
        public V call(Callable<V> callable) throws Exception {
            return timeLimiter.callWithTimeout(callable, duration, timeUnit, true);
        }
    }
}
