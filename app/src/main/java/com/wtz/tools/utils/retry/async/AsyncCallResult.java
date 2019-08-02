package com.wtz.tools.utils.retry.async;

public interface AsyncCallResult<V> {

    void onResult(V result);

    void onException(Throwable t);

}
