package com.wtz.tools.utils.network.socket.client;


public interface ISocketClient {
    void connect(ISocketStateListener listener);
    boolean isConnectionComplete();
    void cancelConnection();
    void disconnect();
    void release();
    boolean isActive();
    void send(byte[] data);
}

