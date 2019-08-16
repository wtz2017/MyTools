package com.wtz.tools.utils.network.socket.client;

public interface ISocketStateListener {
    void onConnectSuccessful();
    void onConnectFailed(String error);
    void onDisconnected(String error);
    void onHeartbeatTime();
    void onReceiveData(byte[] data);
}
