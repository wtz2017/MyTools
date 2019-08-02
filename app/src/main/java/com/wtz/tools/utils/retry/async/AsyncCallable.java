package com.wtz.tools.utils.retry.async;

public interface AsyncCallable<V> {

    void call(AsyncCallResult<V> result) throws Exception;

}
