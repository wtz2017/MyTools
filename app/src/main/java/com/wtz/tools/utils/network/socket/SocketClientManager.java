package com.wtz.tools.utils.network.socket;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.wtz.tools.utils.network.socket.client.ISocketClient;
import com.wtz.tools.utils.network.socket.client.ISocketStateListener;
import com.wtz.tools.utils.network.socket.codec.ICodec;

import java.util.Random;


/**
 * 何时重连、如何重连：都交给 SocketManager 管理，不由外层业务干涉
 */
public class SocketClientManager<T> {
    private static final String TAG = "SocketClientManager";

    private ISocketClient mSocketClient;
    private ICodec<T> mCodec;

    private IActiveListener mActiveListener;
    private IHeartbeatListener mHeartbeatListener;
    private IDataListener<T> mDataReceiveListener;

    private ReconnectHandler mReconnectHandler;

    /**
     * 正常初始化中，还没有结果
     */
    private boolean isIniting;

    /**
     * 初始化基本数据已经完成，不管是否已经连接成功
     */
    private boolean isInitialized;

    private static final int MSG_CONNECT = 1;
    private static final int MSG_SEND_DATA = 2;
    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private static final long MONITOR_INTERVAL = 10 * 60 * 1000;

    public interface IActiveListener {
        void onResult(boolean isActive);
    }

    public interface IHeartbeatListener {
        void onHeartbeatTime();
    }

    public interface IDataListener<T> {
        void onReceive(T msg);
    }

    public interface IReconnectDelay {
        /**
         * 获取重连延时间隔，单位：毫秒(ms)
         */
        long getDelayMillisTime();

        /**
         * 重置延时，用于新的一轮延时
         */
        void reset();
    }

    public SocketClientManager(ISocketClient socketClient, ICodec codec) {
        mSocketClient = socketClient;
        mCodec = codec;
    }

    public void init(IActiveListener activeListener, final IHeartbeatListener heartbeatListener,
                     IDataListener dataListener, IReconnectDelay reconnectDelay) throws Exception {
        Log.d(TAG, "init isInitialized=" + isInitialized);
        if (isIniting || isInitialized) {
            return;
        }
        isIniting = true;

        if (activeListener == null) throw new NullPointerException("IActiveListener is null");
        if (heartbeatListener == null) throw new NullPointerException("IHeartbeatListener is null");
        if (dataListener == null) throw new NullPointerException("IDataListener is null");

        mActiveListener = activeListener;
        mHeartbeatListener = heartbeatListener;
        mDataReceiveListener = dataListener;

        if (reconnectDelay == null) {
            mReconnectHandler = new ReconnectHandler(new DefaultReconnectDelay(), mUiHandler);
        } else {
            mReconnectHandler = new ReconnectHandler(reconnectDelay, mUiHandler);
        }

        initWorkThread();
        handleConnect();

        isInitialized = true;
        isIniting = false;

        startMonitor();
    }

    private void startMonitor() {
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mUiHandler.removeCallbacks(this);
                if (!isInitialized) {
                    // 已经释放了
                    return;
                }
                if (!isWorkThreadAlive()) {
                    initWorkThread();
                }
                if (!mSocketClient.isActive()) {
                    mReconnectHandler.startReconnect();
                }
            }
        }, MONITOR_INTERVAL);
    }

    private void initWorkThread() {
        mWorkThread = new HandlerThread("socket-thread");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CONNECT:
                        disconnect();
                        connect();
                        break;
                    case MSG_SEND_DATA:
                        send((T) msg.obj);
                        break;
                }
            }
        };
    }

    private boolean isWorkThreadAlive() {
        boolean ret = mWorkThread != null && mWorkThread.isAlive() && mWorkThread.getLooper() != null;
        if (!ret) {
            Log.e(TAG, "work thread is not alive");
        }
        return ret;
    }

    private void handleConnect() {
        if (!isWorkThreadAlive()) {
            initWorkThread();
        }
        mWorkHandler.sendEmptyMessage(MSG_CONNECT);
    }

    private void connect() {
        mSocketClient.connect(mSocketStateListener);
    }

    private ISocketStateListener mSocketStateListener = new ISocketStateListener() {
        @Override
        public void onConnectSuccessful() {
            Log.d(TAG, "onConnectSuccessful");
            mReconnectHandler.stopReconnect();
            callbackActiveListener(true);
        }

        @Override
        public void onConnectFailed(String error) {
            Log.d(TAG, "onConnectFailed: " + error);
            mReconnectHandler.startReconnect();
            callbackActiveListener(false);
        }

        @Override
        public void onDisconnected(String error) {
            Log.d(TAG, "onDisconnected: " + error);
            mReconnectHandler.startReconnect();
            callbackActiveListener(false);
        }

        @Override
        public void onHeartbeatTime() {
            Log.d(TAG, "onHeartbeatTime");
            callbackHeartbeatListener();
        }

        @Override
        public void onReceiveData(byte[] data) {
            T msg = mCodec.decode(data);
            Log.d(TAG, "onReceiveData: " + msg);
            callbackDataListener(msg);
        }
    };

    private void callbackActiveListener(final boolean isActive) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mActiveListener != null) {
                    mActiveListener.onResult(isActive);
                }
            }
        });
    }

    private void callbackHeartbeatListener() {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mHeartbeatListener != null) {
                    mHeartbeatListener.onHeartbeatTime();
                }
            }
        });
    }

    private void callbackDataListener(final T msg) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDataReceiveListener != null) {
                    mDataReceiveListener.onReceive(msg);
                }
            }
        });
    }

    public boolean isActive() {
        return isInitialized && isWorkThreadAlive() && mSocketClient.isActive();
    }

    public void sendData(T data) {
        if (!isActive()) {
            Log.d(TAG, "send: socket is not active");
            return;
        }
        if (data == null) {
            Log.d(TAG, "send: data is null");
            return;
        }

        handleSendData(data);
    }

    private void handleSendData(T data) {
        if (isWorkThreadAlive()) {
            Message msg = mWorkHandler.obtainMessage(MSG_SEND_DATA, data);
            mWorkHandler.sendMessage(msg);
        }
    }

    private void send(T data) {
        Log.d(TAG, "to send data: " + data);
        mSocketClient.send(mCodec.encode(data));
    }

    public void release() {
        Log.d(TAG, "release isInitialized: " + isInitialized);
        // 从上游到下游的顺序依次释放
        // 1. 立即更改状态，阻止继续提交任务
        isInitialized = false;
        isIniting = false;

        // 2. 移除已经 post 到 workThread 中的任务，中止工作线程
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkThread.quit();

        // 3. 断开长连接，以中断正在执行的通信任务
        disconnect();
        mSocketClient.release();

        // 4. 移除执行完成进行回调的结果
        mUiHandler.removeCallbacksAndMessages(null);
    }

    private void disconnect() {
        if (!mSocketClient.isConnectionComplete()) {
            mSocketClient.cancelConnection();
        }
        if (mSocketClient.isActive()) {
            mSocketClient.disconnect();
        }
    }

    class ReconnectHandler implements Runnable {
        private static final String TAG = "ReconnectHandler";
        private boolean isRunning;
        private IReconnectDelay reconnectDelay;
        private Handler handler;

        public ReconnectHandler(IReconnectDelay reconnectDelay, Handler handler) {
            this.reconnectDelay = reconnectDelay;
            this.handler = handler;
        }

        public void startReconnect() {
            Log.d(TAG, "startReconnect isRunning=" + isRunning);
            if (isRunning) {
                return;
            }
            isRunning = true;

            handler.removeCallbacks(this);
            handler.postDelayed(this, reconnectDelay.getDelayMillisTime());
        }

        public void stopReconnect() {
            handler.removeCallbacks(this);
            reconnectDelay.reset();
            isRunning = false;
        }

        @Override
        public void run() {
            // 真正开始重连
            handleConnect();
            isRunning = false;
        }
    }

    class DefaultReconnectDelay implements IReconnectDelay {
        private static final String TAG = "DefaultReconnectDelay";
        private static final long BASE_MULTIPLIER = 30000;// 倍乘基数，单位：毫秒
        private static final long MAX_DELAY = 1920000;// 最大延时，单位：毫秒

        private int retryNumber;// 重试第几次

        @Override
        public long getDelayMillisTime() {
            long baseTime = getBaseMillisTime(retryNumber);
            long nextBaseTime = getBaseMillisTime(retryNumber + 1);
            long randomTime = getRandomSecondsTime((int) (nextBaseTime / 1000)) * 1000;
            long result = baseTime + randomTime;
            if (result > MAX_DELAY) {
                result = MAX_DELAY;
            }
            Log.d(TAG, "retryNumber=" + retryNumber + ", delay=" + result);
            retryNumber++;
            return result;
        }

        private long getBaseMillisTime(int exponent) {
            return Math.round(BASE_MULTIPLIER * Math.pow(2, exponent));
        }

        /**
         * 要考虑长连接服务器重启时所有终端都断开一起重连的并发情况
         */
        private int getRandomSecondsTime(int boundSeconds) {
            return new Random().nextInt(boundSeconds);
        }

        @Override
        public void reset() {
            retryNumber = 0;
        }
    }

}
