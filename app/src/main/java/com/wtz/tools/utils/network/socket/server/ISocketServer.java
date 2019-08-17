package com.wtz.tools.utils.network.socket.server;

public interface ISocketServer {
    void start(int port, ISocketDataListener socketDataListener);
    void sendToAll(byte[] data);
    void stop();
}
