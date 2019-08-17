package com.wtz.tools.utils.network.socket.test;

import android.util.Log;

import com.wtz.tools.utils.network.socket.codec.impl.ProtobufCodec;
import com.wtz.tools.utils.network.socket.server.ISocketDataListener;
import com.wtz.tools.utils.network.socket.server.impl.SimpleSocketServer;


public class SocketServerSample {
    private static final String TAG = "SocketServerSample";
    private SimpleSocketServer mSimpleSocketServer;

    public void start(int port) {
        mSimpleSocketServer = new SimpleSocketServer();
        mSimpleSocketServer.start(port, new ISocketDataListener() {
            @Override
            public void onReceive(String clientId, byte[] data) {
                String received = parse(data);
                Log.d(TAG, "onReceive: clientId=" + clientId
                        + ",data byte size=" + data.length + ",data=" + received);

                byte[] responseBuf = pack();
                mSimpleSocketServer.sendToAll(responseBuf);
            }
        });
    }

    public void stop() {
        mSimpleSocketServer.stop();
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
