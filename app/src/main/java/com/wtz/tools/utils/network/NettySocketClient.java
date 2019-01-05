package com.wtz.tools.utils.network;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * 使用netty版本："io.netty:netty-all:4.1.10.Final"
 */
public class NettySocketClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8007;

    private EventLoopGroup mEventLoopGroup;
    private Bootstrap mBootstrap;
    private Channel mChannel;

    private boolean isConnect;
    private boolean isConnecting;

    public static void main(String[] args) throws Exception {
        NettySocketClient clientSocket = new NettySocketClient();
        clientSocket.connect();
        Thread.sleep(3000);
        clientSocket.sendMessage("hello");
        clientSocket.sendMessage("world");
    }

    public NettySocketClient() {
        mEventLoopGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap();
        mBootstrap.group(mEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // p.addLast(new LoggingHandler(LogLevel.INFO));
                        // 5s未发送数据，回调 userEventTriggered
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                        p.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                        // 消息处理
                        p.addLast(new MessageHandler());
                    }
                });
    }

    public void connect() {
        if (isConnect || isConnecting) {
            return;
        }
        isConnecting = true;
        try {
            mBootstrap.connect(HOST, PORT).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        //连接成功
                        isConnect = true;
                        mChannel = channelFuture.channel();
                        System.out.println("connect success...mChannel = " + mChannel);
                    } else {
                        //连接失败
                        isConnect = false;
                        System.out.println("connect fail");
                    }
                    isConnecting = false;
                }
            }).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        System.out.println("sendMessage mChannel.isOpen: " + (mChannel != null ? mChannel.isOpen() : false));
        try {
            if (mChannel != null && mChannel.isOpen()) {
                // Netty中的消息传递，都必须以字节的形式，以ChannelBuffer为载体传递。
                // 简单的说，就是你想直接写个字符串过去，对不起，抛异常:
                // unsupported message type: String (expected: ByteBuf, FileRegion)
                // 可以添加对应的编解码器来避免上述异常
                mChannel.writeAndFlush(msg).sync();
                System.out.println("sendMessage success: " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (mChannel != null && mChannel.isOpen()) {
            mChannel.close();
        }
        mEventLoopGroup.shutdownGracefully();
    }

    public class MessageHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println("channelActive");
        }

//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            System.out.println("channelRead: " + msg);
//        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("channelRead0: " + msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            System.out.println("channelReadComplete");
            ctx.flush();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("userEventTriggered: " + evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.WRITER_IDLE)) {
                    System.out.println("heartbeat");
                    ctx.channel().writeAndFlush("heartbeat");
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
    }

}
