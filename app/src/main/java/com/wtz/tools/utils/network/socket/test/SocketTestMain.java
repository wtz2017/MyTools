package com.wtz.tools.utils.network.socket.test;

import android.os.Handler;
import android.os.Looper;

public class SocketTestMain {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8989;

    private SimpleSocketServer mSimpleSocketServer;
    private SocketClientSample mSocketClientSample;

    public void start() {
        mSimpleSocketServer = new SimpleSocketServer();
        mSimpleSocketServer.startTCPServer(PORT);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSocketClientSample = new SocketClientSample(IP, PORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSocketClientSample.init();
//                        mSocketClientSample.sendData();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSocketClientSample.sendData();


//                        mSocketClientSample.release();
                    }
                }).start();
            }
        }, 3000);
    }

    public void stop() {
        mSocketClientSample.release();
    }
}
