package com.wtz.tools.utils.network.socket.test;

import android.os.Handler;
import android.os.Looper;

public class SocketTestMain {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8989;

    private SocketServerSample mSocketServerSample;
    private SocketClientSample mSocketClientSample;
    private SocketClientSample mSocketClientSample2;

    private Handler handler = new Handler(Looper.getMainLooper());

    public void start() {
        mSocketServerSample = new SocketServerSample();
        mSocketServerSample.start(PORT);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSocketClientSample = new SocketClientSample(IP, PORT);
                            mSocketClientSample2 = new SocketClientSample(IP, PORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSocketClientSample.init();
                        mSocketClientSample2.init();
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSocketClientSample.release();
            }
        }, 15000);
    }

    public void stop() {
        mSocketServerSample.stop();
        mSocketClientSample.release();
        mSocketClientSample2.release();
        handler.removeCallbacksAndMessages(null);
    }
}
