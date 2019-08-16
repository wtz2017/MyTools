package com.wtz.tools.utils.network.socket.test;

import android.util.Log;

import com.wtz.tools.utils.network.socket.codec.impl.ProtobufCodec;
import com.wtz.tools.utils.network.socket.codec.impl.StringCodec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class SimpleSocketServer {
    private static final String TAG = "SimpleSocketServer";

    private Thread mThread;
    private boolean stop;

    public void startTCPServer(final int port) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = null;
                try {
                    // 1、创建ServerSocket服务器套接字
                    server = new ServerSocket(port);
                    // 设置连接超时时间，不设置，则是一直阻塞等待
//                    server.setSoTimeout(8000);

                    while (!stop) {
                        // 2、等待被连接。在连接超时时间内连接有效，超时则抛异常，
                        Socket client = server.accept();
                        Log.d(TAG, "accept a client connected...");
                        // 设置读取流的超时时间，不设置，则是一直阻塞读取
                        client.setSoTimeout(5000);

                        // 3、获取输入流和输出流
                        InputStream inputStream = client.getInputStream();
                        OutputStream outputStream = client.getOutputStream();

                        // 4、读取数据
                        byte[] buf = new byte[1024];
                        int len = inputStream.read(buf);
                        String received = parse(buf);
                        Log.d(TAG, "received data from client: " + received);

                        // 5、发送响应数据
                        byte[] responseBuf = pack();
                        outputStream.write(responseBuf, 0, responseBuf.length);
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Exception：" + e.toString());
                    e.printStackTrace();
                } finally {
                    if (server != null) {
                        try {
                            server.close();
                            server = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        stop = false;
        mThread.start();
    }

    public void stop() {
        stop = true;
        try {
            mThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parse(byte[] buf) {
//        StringCodec stringCodec = new StringCodec(Charset.forName("UTF-8"));
//        String receData = stringCodec.decode(buf);

        ProtobufCodec<PersonProtos.Person> protobufCodec = new ProtobufCodec<>(PersonProtos.Person.class);
        PersonProtos.Person person = protobufCodec.decode(buf);
        String receData = person.toString();

        return receData;
    }

    private byte[] pack() {
//        StringCodec stringCodec = new StringCodec(Charset.forName("UTF-8"));
//        byte[] responseBuf = stringCodec.encode("Hi, I am Server");

        ProtobufCodec<PersonProtos.Person> protobufCodec = new ProtobufCodec<>(PersonProtos.Person.class);
        byte[] responseBuf = protobufCodec.encode(getPerson());

        return responseBuf;
    }

    private PersonProtos.Person getPerson() {
        PersonProtos.Person.Builder personBuilder = PersonProtos.Person.newBuilder();
        personBuilder.setName("小二");
        personBuilder.setId(102);
        personBuilder.setEmail("test2@163.com");

        PersonProtos.Person.PhoneNumber.Builder phone = PersonProtos.Person.PhoneNumber.newBuilder();
        phone.setNumber("18610000003");
        phone.setType(PersonProtos.Person.PhoneType.HOME);
        PersonProtos.Person.PhoneNumber.Builder phone2 = PersonProtos.Person.PhoneNumber.newBuilder();
        phone2.setNumber("18610000004");
        phone2.setType(PersonProtos.Person.PhoneType.WORK);
        personBuilder.addPhone(phone);
        personBuilder.addPhone(phone2);

        return personBuilder.build();
    }

}
