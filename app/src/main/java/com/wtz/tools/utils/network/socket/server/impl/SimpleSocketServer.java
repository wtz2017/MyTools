package com.wtz.tools.utils.network.socket.server.impl;

import android.util.Log;


import com.wtz.tools.utils.network.socket.codec.impl.BaseCodec;
import com.wtz.tools.utils.network.socket.server.ISocketDataListener;
import com.wtz.tools.utils.network.socket.server.ISocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleSocketServer implements ISocketServer {
    private static final String TAG = "SimpleSocketServer";

    private Thread mMainThread;
    private ExecutorService mThreadPool;
    private static final int MAX_THREAD_NUM = 100;
    private boolean stop;
    private ConcurrentHashMap<String, Socket> mClientMap;
    private LinkedBlockingQueue<byte[]> mSendQueue;
    private ISocketDataListener mSocketDataListener;

    @Override
    public void start(final int port, ISocketDataListener socketDataListener) {
        mSocketDataListener = socketDataListener;
        mClientMap = new ConcurrentHashMap();
        mSendQueue = new LinkedBlockingQueue();
        mThreadPool = Executors.newFixedThreadPool(MAX_THREAD_NUM);
        mThreadPool.submit(new MessageSender());
        mMainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket(port);
                    while (!stop) {
                        Socket client = server.accept();
                        String id = getClientId();
                        Log.d(TAG, "accept a client: " + client.getInetAddress());
                        mClientMap.put(id, client);
                        mThreadPool.submit(new MessageReader(id, client));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (server != null) {
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        mMainThread.start();
    }

    private String getClientId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void sendToAll(byte[] data) {
        Log.d(TAG, "sendToAll data size=" + data.length + ", stop=" + stop);
        if (stop) {
            return;
        }
//        mSendQueue.offer(data);
        try {
            mSendQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        stop = true;
        try {
            mMainThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mThreadPool.shutdownNow();
        mClientMap.clear();
        mSendQueue.clear();
    }

    class MessageReader implements Runnable {
        private String id;
        private Socket client;

        public MessageReader(String id, Socket client) {
            this.id = id;
            this.client = client;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            try {
                inputStream = client.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (true) {
                try {
                    byte[] headBytes = new byte[5];//与客户端约定header最大字节数
                    int len = inputStream.read(headBytes);
                    Log.d(TAG, "headBytes=" + headBytes + ", len=" + len);
                    if (len == -1) {
                        mClientMap.remove(id);
                        break;
                    }
                    int[] headerInfo = BaseCodec.readRawVarint32Header(headBytes);
                    int varintByteCount = headerInfo[0];// 头占用字节数
                    int varintValue = headerInfo[1];// 头中记载的消息体长度
                    int remainBytesSize = varintValue - (len - varintByteCount);//还需要读的字节数
                    Log.d(TAG, "varintByteCount=" + varintByteCount + ",varintValue=" + varintValue
                            + ", remainBytesSize=" + remainBytesSize);
                    byte[] remainBytes = new byte[remainBytesSize];
                    inputStream.read(remainBytes);
                    byte[] result = BaseCodec.concat(headBytes, remainBytes);
                    if (mSocketDataListener != null) {
                        mSocketDataListener.onReceive(id, result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MessageSender implements Runnable {
        @Override
        public void run() {
            while (true) {
                byte[] data;
                try {
                    data = mSendQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                if (mClientMap.isEmpty()) {
                    Log.d(TAG, "MessageSender mClientMap is Empty");
                    continue;
                }
                Iterator<Map.Entry<String, Socket>> entries = mClientMap.entrySet().iterator();
                OutputStream outputStream;
                while (entries.hasNext()) {
                    Map.Entry<String, Socket> entry = entries.next();
                    Log.d(TAG, "MessageSender to client id = " + entry.getKey());
                    try {
                        outputStream = entry.getValue().getOutputStream();
                        outputStream.write(data, 0, data.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
