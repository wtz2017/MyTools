package com.wtz.tools.utils.network;

import android.content.Context;
import android.util.Log;

import com.wtz.tools.utils.event.RxBus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkhttpWebSocket {
    private static final String TAG = OkhttpWebSocket.class.getSimpleName();

    private Context mContext;
    private String mUrl;
    private OkHttpClient mHttpClient;
    private WebSocket mWebSocket;
    private boolean isConnecting;

    /**
     * Code must be in range [1000,5000)
     * 1004/1005/1006/1012-2999 is reserved and may not be used
     */
    private static final int CLOSE_CODE_NORMAL = 4567;

    public static class WebsocketMsg {
        public String content;

        public WebsocketMsg(String content) {
            this.content = content;
        }
    }

    public static void test(Context context) {
        OkhttpWebSocket webSocket = new OkhttpWebSocket(context, "ws://echo.websocket.org");
        webSocket.connect();
    }

    public OkhttpWebSocket(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    public synchronized void connect() {
        boolean isNetConnected = NetworkDeviceUtils.isNetworkConnect(mContext);
        Log.d(TAG,"connect...network is connected = " + isNetConnected
                + ", isConnecting = " + isConnecting + ", mWebSocket = " + mWebSocket);
        if (!isNetConnected || isConnecting || mWebSocket != null) {
            return;
        }
        isConnecting = true;
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    // 设置心跳，具体实现在 okhttp3.internal.http2.Http2Connection.PingRunnable
                    .pingInterval(30, TimeUnit.SECONDS)
                    .build();
        }
        Request request = new Request.Builder().url(mUrl).build();
        DefaultWebSocketListener listener = new DefaultWebSocketListener();
        mHttpClient.newWebSocket(request, listener);
    }

    public synchronized boolean isConnected() {
        return mWebSocket != null;
    }

    public synchronized void send(String message) {
        if (!NetworkDeviceUtils.isNetworkConnect(mContext)) {
            RxBus.getInstance().send(new WebsocketMsg("Please connect network!"));
            return;
        }
        if (mWebSocket != null) {
            mWebSocket.send(message);
        }
    }

    public void close() {
        close(CLOSE_CODE_NORMAL, "normal close!");
    }

    public synchronized void close(int code, String reason) {
        if (mWebSocket != null) {
            try {
                mWebSocket.close(code, reason);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mWebSocket = null;
        }
    }

    public synchronized void destroy() {
        close();
        if (mHttpClient != null) {
            mHttpClient.dispatcher().executorService().shutdown();
            mHttpClient = null;
        }
        mContext = null;
    }

    public class DefaultWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "onOpen: " + response.toString());
            mWebSocket = webSocket;
            isConnecting = false;

            // TODO: 2019/1/2 test ------
//            send("hello!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG,"receive String message: " + text);
            RxBus.getInstance().send(new WebsocketMsg(text));
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d(TAG,"receive Byte message: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.d(TAG,"onClosing: " + code + " " + reason);
            mWebSocket = null;
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG,"onClosed: " + code + " " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d(TAG,"onFailure: " + t.toString());
            t.printStackTrace();
            mWebSocket = null;
            isConnecting = false;
            // TODO: 2019/1/2 do retry
        }
    }

}
