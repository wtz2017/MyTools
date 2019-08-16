package com.wtz.tools.utils.network.socket.client.impl;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

public class BaseSocketClient {

    protected String mIp;
    protected int mPort;
    protected long mHeartbeatInterval;
    protected TimeUnit mHeartbeatUnit;

    public BaseSocketClient(String ip, int port, long heartbeatInterval, TimeUnit heartbeatUnit) throws Exception {
        if (TextUtils.isEmpty(ip) || port < 0)
            throw new Exception("mIp or mPort is invalid");
        if (heartbeatInterval <= 0 || heartbeatUnit == null)
            throw new Exception("heartbeatInterval or unit is invalid");

        this.mIp = ip;
        this.mPort = port;
        this.mHeartbeatInterval = heartbeatInterval;
        this.mHeartbeatUnit = heartbeatUnit;
    }

}
