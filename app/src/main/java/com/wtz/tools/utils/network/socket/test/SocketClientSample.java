package com.wtz.tools.utils.network.socket.test;


import com.wtz.tools.utils.network.socket.SocketClientManager;
import com.wtz.tools.utils.network.socket.client.impl.NettySocketClient;
import com.wtz.tools.utils.network.socket.codec.impl.ProtobufCodec;
import com.wtz.tools.utils.network.socket.codec.impl.StringCodec;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class SocketClientSample {
    private static final String TAG = "SocketClientSample";

    private String mIp;
    private int mPort;

//    private SocketClientManager<String> socketClientManager;
    private SocketClientManager<PersonProtos.Person> socketClientManager;

    public SocketClientSample(String ip, int port) throws Exception {
        mIp = ip;
        mPort = port;

//        socketClientManager = new SocketClientManager<>(
//                new AsyncSocketClient(ip, port, 5, TimeUnit.SECONDS),
//                new StringCodec(Charset.forName("UTF-8")));

//        socketClientManager = new SocketClientManager<>(
//                new NettySocketClient(ip, port, 5, TimeUnit.SECONDS),
//                new StringCodec(Charset.forName("UTF-8")));

        socketClientManager = new SocketClientManager<>(
                new NettySocketClient(ip, port, 5, TimeUnit.SECONDS),
                new ProtobufCodec(PersonProtos.Person.class));
    }

    public void init() {
        try {
            socketClientManager.init(activeListener, heartbeatListener, dataListener, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SocketClientManager.IActiveListener activeListener = new SocketClientManager.IActiveListener() {
        @Override
        public void onResult(boolean isActive) {
            if (isActive) {
                // TODO 发送 sync 数据
            }
        }
    };

    private SocketClientManager.IHeartbeatListener heartbeatListener = new SocketClientManager.IHeartbeatListener() {
        @Override
        public void onHeartbeatTime() {
            // TODO 发送 心跳 数据
            sendData();
        }
    };

//    private SocketClientManager.IDataListener<String> dataListener = new SocketClientManager.IDataListener<String>() {
//        @Override
//        public void onReceive(String msg) {
////            Log.d(TAG, "onReceive: " + msg);
//        }
//    };

    private SocketClientManager.IDataListener<PersonProtos.Person> dataListener = new SocketClientManager.IDataListener<PersonProtos.Person>() {
        @Override
        public void onReceive(PersonProtos.Person msg) {
//            Log.d(TAG, "onReceive: " + msg);
        }
    };

    public void sendData() {
//        socketClientManager.sendData(getStringData());
        socketClientManager.sendData(getPerson());
    }

    private String getStringData() {
        StringCodec stringCodec = new StringCodec(Charset.forName("UTF-8"));
        String content = "{\n" +
                "  \"id\": 123,\n" +
                "  \"desc\": \"请输入你要格式化的JSON字符串\"\n" +
                "}";
        return content;
    }

    private PersonProtos.Person getPerson() {
        PersonProtos.Person.Builder personBuilder = PersonProtos.Person.newBuilder();
        personBuilder.setName("王五");
        personBuilder.setId(100);
        personBuilder.setEmail("test@163.com");

        PersonProtos.Person.PhoneNumber.Builder phone = PersonProtos.Person.PhoneNumber.newBuilder();
        phone.setNumber("18610000001");
        phone.setType(PersonProtos.Person.PhoneType.HOME);
        PersonProtos.Person.PhoneNumber.Builder phone2 = PersonProtos.Person.PhoneNumber.newBuilder();
        phone2.setNumber("18610000002");
        phone2.setType(PersonProtos.Person.PhoneType.WORK);
        personBuilder.addPhone(phone);
        personBuilder.addPhone(phone2);

        return personBuilder.build();
    }

    public void release() {
        socketClientManager.release();
    }
}
