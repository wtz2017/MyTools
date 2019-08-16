package com.wtz.tools.utils.network.socket.client.impl;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Cancellable;
import com.wtz.tools.utils.network.socket.client.ISocketClient;
import com.wtz.tools.utils.network.socket.client.ISocketStateListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 使用 AndroidAsync 库实现 SocketClient
 */
public class AsyncSocketClient extends BaseSocketClient implements ISocketClient {

    private static final String TAG = "AsyncSocketClient";

    private AsyncSocket mSocket;
    private ISocketStateListener mSocketStateListener;

    private Cancellable mConnectAction;
    private static final int STATE_NOT_START = -1;
    private static final int STATE_CONNECTING = 0;
    private static final int STATE_CONNECT_COMPLETE = 1;
    private int mConnectionState = STATE_NOT_START;

    private ScheduledExecutorService mScheduledService;
    private ScheduledFuture<?> mHeartbeatScheduledFuture;

    private ConnectCallback mConnectCallback = new ConnectCallback() {
        @Override
        public void onConnectCompleted(Exception ex, AsyncSocket socket) {
            Log.d(TAG, "ConnectCallback onConnectCompleted, EX = " + ex);
            mConnectionState = STATE_CONNECT_COMPLETE;
            if (ex == null) {
                mSocket = socket;
                mSocket.setDataCallback(mReceiveDataCallback);
                mSocket.setEndCallback(mDisconnectCallback);
                mSocketStateListener.onConnectSuccessful();

                if (mScheduledService == null) {
                    mScheduledService = Executors.newScheduledThreadPool(1);
                }
                mHeartbeatScheduledFuture = mScheduledService.scheduleWithFixedDelay(mHeartbeatRunnable,
                        mHeartbeatInterval , mHeartbeatInterval, mHeartbeatUnit);
            } else {
                mSocketStateListener.onConnectFailed(ex.toString());
            }
        }
    };

    private Runnable mHeartbeatRunnable = new Runnable() {
        @Override
        public void run() {
            mSocketStateListener.onHeartbeatTime();
        }
    };

    private DataCallback mReceiveDataCallback = new DataCallback() {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            Log.d(TAG, "mReceiveDataCallback onDataAvailable ByteBufferList size: " + bb.size());
            mSocketStateListener.onReceiveData(bb.getAllByteArray());
        }
    };

    private CompletedCallback mDisconnectCallback = new CompletedCallback() {
        @Override
        public void onCompleted(Exception ex) {
            Log.d(TAG, "mDisconnectCallback onCompleted: " + ex);
            if (mHeartbeatScheduledFuture != null) {
                mHeartbeatScheduledFuture.cancel(true);
                mHeartbeatScheduledFuture = null;
            }
            mSocketStateListener.onDisconnected("" + ex);
        }
    };

    public AsyncSocketClient(String ip, int port, long heartbeatInterval, TimeUnit heartbeatUnit) throws Exception {
        super(ip, port, heartbeatInterval, heartbeatUnit);
    }

    @Override
    public void connect(ISocketStateListener listener) {
        if (mConnectionState == STATE_CONNECTING) {
            Log.d(TAG, "earlier connections is doing");
            return;
        }
        if (isActive()) {
            Log.d(TAG, "socket is already connected");
            return;
        }
        mConnectionState = STATE_CONNECTING;

        mSocketStateListener = listener;
        mConnectAction = AsyncServer.getDefault().connectSocket(mIp, mPort, mConnectCallback);
    }

    @Override
    public boolean isConnectionComplete() {
        return mConnectionState == STATE_CONNECT_COMPLETE;
    }

    @Override
    public void cancelConnection() {
        if (isConnectionComplete()) {
            mConnectAction = null;
            disconnect();
            return;
        }
        if (mConnectAction != null && !mConnectAction.isCancelled()) {
            mConnectAction.cancel();
            mConnectAction = null;
            Log.d(TAG, "ConnectAction is canceled");
        }
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect");
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

    @Override
    public void release() {
        try {
            if (mScheduledService != null) {
                mScheduledService.shutdownNow();
                mScheduledService = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        return mSocket != null && mSocket.isOpen();
    }

    @Override
    public void send(final byte[] data) {
        if (mSocket != null) {
            Util.writeAll(mSocket, data, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    boolean success = ex == null;
                    if (!success) {
                        Log.d(TAG, String.format("send failed, message = %s", ex.getMessage()));
                    } else {
                        Log.d(TAG, "sent successfully " + data.length + " bytes data");
                    }
                }
            });
        } else {
            Log.e(TAG, "send: mSocket is null");
        }
    }

}
