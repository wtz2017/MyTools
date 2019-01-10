package com.wtz.tools.utils.data_transfer_format.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtobufDemo {

    public static void testProtobuf() {
        PersonProtos.Person.Builder personBuilder = PersonProtos.Person.newBuilder();
        personBuilder.setName("王五");
        personBuilder.setId(100);
        personBuilder.setEmail("test@163.com");

        PersonProtos.Person.PhoneNumber.Builder phone = PersonProtos.Person.PhoneNumber.newBuilder();
        phone.setNumber("18610000001");
        PersonProtos.Person.PhoneNumber.Builder phone2 = PersonProtos.Person.PhoneNumber.newBuilder();
        phone2.setNumber("18610000002");
        personBuilder.addPhone(phone);
        personBuilder.addPhone(phone2);

        PersonProtos.Person person = personBuilder.build();

        serialize1(person);
        serialize2(person);
    }

    private static void serialize1(PersonProtos.Person person) {
        // 序列化方式一：获取字节数组，适用于SOCKET或者保存在磁盘
        byte[] data = person.toByteArray();
        // 反序列化
        PersonProtos.Person result = null;
        try {
            result = PersonProtos.Person.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println(result.getName() + "\n" + result.getId() + "\n"
                + result.getEmail() + "\n" + result.getPhoneList());
    }

    private static void serialize2(PersonProtos.Person person) {
        // 序列化方式二：粘包，将一个或者多个protobuf对象字节写入stream，适合RPC调用、Socket传输
        // 在序列化的字节数组之前，添加一个varint32的数字表示字节数组的长度；
        // 那么在反序列化时，可以通过先读取varint，然后再依次读取此长度的字节；
        // 这种方式有效的解决了socket传输时如何“拆包”“封包”的问题。在Netty中，使用了同样的技巧。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 生成一个由[字节长度][字节数据]组成的package，特别适合RPC场景
        try {
            person.writeDelimitedTo(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 反序列化，从steam中读取一个或者多个protobuf字节对象
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        PersonProtos.Person result = null;
        try {
            result = PersonProtos.Person.parseDelimitedFrom(byteArrayInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result.getName() + "\n" + result.getId() + "\n"
                + result.getEmail() + "\n" + result.getPhoneList());
    }

    private void testNetty() {
        // protobuf 与 Netty 结合使用：
        // void initChannel(SocketChannel ch) throws Exception {
        // 	ch.pipeline().addLast(new ProtobufVarint32FrameDecoder())
        // 		         .addLast(new ProtobufDecoder(PersonProtos.Person.getDefaultInstance()))
        // 		         .addLast(new ProtobufVarint32LengthFieldPrepender())
        // 		         .addLast(new ProtobufEncoder())
        // 		         .addLast(new ProtobufServerHandler());
        // }
        // channel内部维护一个 pipeline，类似一个 filter 链表一样，所有的 socket 读写都会经过；
        // 对于 write 操作（outbound）会从 pipeline 列表的 last-->first 方向依次调用 Encoder处理器；
        // 对于 read 操作（inbound）会从 pipeline 列表的 first-->last 依次调用 Decoder处理器；
        // 此外 Encoder 处理对于 read 操作不起效，Decoder 处理器对 write 操作不起效；
        //
        // ProtobufEncoder：内部直接使用了message.toByteArray()将字节数据放入bytebuf中输出，交由下一个encoder处理。
        // ProtobufVarint32LengthFieldPrepender：因为ProtobufEncoder只是将message的各个filed按照规则输出了，并没有serializedSize，所以这个Encoder的作用就是在ProtobufEncoder生成的字节数组前，prepender一个varint32数字，表示serializedSize。
        // ProtobufVarint32FrameDecoder：这个decoder和Prepender做的工作正好对应，作用就是“成帧”，根据seriaziedSize读取足额的字节数组，即一个完整的package。
        // ProtobufDecoder：和ProtobufEncoder对应，这个Decoder需要指定一个默认的instance，decoder将会解析byteArray，并根据format规则为此instance中的各个filed赋值。
    }

}
