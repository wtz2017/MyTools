package com.wtz.tools.utils.network.socket.client.impl;

import android.util.Log;

import com.wtz.tools.utils.network.socket.client.ISocketClient;
import com.wtz.tools.utils.network.socket.client.ISocketStateListener;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class NettySocketClient extends BaseSocketClient implements ISocketClient {
    private static final String TAG = "NettySocketClient";

    private EventLoopGroup mEventLoopGroup;
    private Bootstrap mBootstrap;
    private ChannelFuture mConnectFuture;
    private Channel mChannel;

    private ISocketStateListener mSocketStateListener;

    private static final int STATE_NOT_START = -1;
    private static final int STATE_CONNECTING = 0;
    private static final int STATE_CONNECT_COMPLETE = 1;
    private int mConnectionState = STATE_NOT_START;

    public NettySocketClient(String ip, int port, final long heartbeatInterval, final TimeUnit unit) throws Exception {
        super(ip, port, heartbeatInterval, unit);

        mEventLoopGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap();
        mBootstrap.group(mEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        Log.d(TAG, "initChannel...");
                        ChannelPipeline p = ch.pipeline();
                        // p.addLast(new LoggingHandler(LogLevel.INFO));

                        // 心跳设置，Idle时间到达后，回调 userEventTriggered
                        p.addLast(new IdleStateHandler(0, heartbeatInterval, 0, unit));

                        // p.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                        // p.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                        p.addLast(new ByteArrayToByteBufEncoder());// 用以发送byte[]

                        // 消息处理
                        p.addLast(new MessageHandler());
                    }
                });
    }

    public class ByteArrayToByteBufEncoder extends MessageToByteEncoder<byte[]> {
        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
            out.writeBytes(msg);
        }
    }

    public class MessageHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            Log.d(TAG, "channelActive");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Log.d(TAG, "channelRead: " + msg);
            if (msg instanceof ByteBuf) {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] bytes = byteBuf.array();
                Log.d(TAG, "channelRead is ByteBuf and bytes size=" + bytes.length);
                mSocketStateListener.onReceiveData(bytes);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            Log.d(TAG, "channelReadComplete");
            ctx.flush();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            Log.d(TAG, "userEventTriggered: " + evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.WRITER_IDLE)) {
                    mSocketStateListener.onHeartbeatTime();
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Log.d(TAG, "channelInactive");
            mSocketStateListener.onDisconnected("unknown");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
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

        try {
            mConnectFuture = mBootstrap.connect(mIp, mPort).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Log.d(TAG, "connect success");
                        //连接成功
                        mChannel = channelFuture.channel();
                        mSocketStateListener.onConnectSuccessful();
                    } else {
                        Log.d(TAG, "connect failed");
                        //连接失败
                        mSocketStateListener.onConnectFailed("unknown");
                    }
                    mConnectionState = STATE_CONNECT_COMPLETE;
                }
            }).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            mSocketStateListener.onConnectFailed(e.toString());
        }
    }

    @Override
    public boolean isConnectionComplete() {
        return mConnectionState == STATE_CONNECT_COMPLETE;
    }

    @Override
    public void cancelConnection() {
        if (isConnectionComplete()) {
            mConnectFuture = null;
            disconnect();
            return;
        }
        if (mConnectFuture != null && !mConnectFuture.isCancelled()) {
            mConnectFuture.cancel(true);
            mConnectFuture = null;
            Log.d(TAG, "mConnectFuture is canceled");
        }
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect");
        if (mChannel != null && mChannel.isOpen()) {
            mChannel.close();
            mChannel = null;
        }
        mEventLoopGroup.shutdownGracefully();
    }

    @Override
    public void release() {

    }

    @Override
    public boolean isActive() {
        return mChannel != null && mChannel.isActive();
    }

    @Override
    public void send(byte[] data) {
        try {
            mChannel.writeAndFlush(data).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
