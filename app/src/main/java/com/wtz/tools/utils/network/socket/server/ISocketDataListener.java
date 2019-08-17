package com.wtz.tools.utils.network.socket.server;

public interface ISocketDataListener {
    void onReceive(String clientId, byte[] data);
}
