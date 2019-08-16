package com.wtz.tools.utils.network.socket.codec;

public interface ICodec<T> {
    byte[] encode(T data);
    T decode(byte[] data);
}
