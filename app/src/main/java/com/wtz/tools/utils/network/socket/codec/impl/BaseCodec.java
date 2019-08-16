package com.wtz.tools.utils.network.socket.codec.impl;

import java.lang.reflect.Array;

public class BaseCodec {

    /**
     * 加上 header 的字节数组
     *
     * @param msgBody 消息 body 字节数组
     * @return
     */
    public byte[] encodeBytes(byte[] msgBody) {
        if (msgBody == null) {
            return new byte[0];
        }

        byte[] msgHead = generateHeader(msgBody.length);
        byte[] result = (byte[]) Array.newInstance(byte.class, msgHead.length + msgBody.length);
        System.arraycopy(msgHead, 0, result, 0, msgHead.length);
        System.arraycopy(msgBody, 0, result, msgHead.length, msgBody.length);

        return result;
    }

    /**
     * generate header by varint32
     * <p>
     * Reference 1: io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
     * Reference 2: https://www.cnblogs.com/tankaixiong/p/6366043.html
     * <p>
     * Varint 是一种紧凑的表示数字的方法。它用一个或多个字节来表示一个数字，值越小的数字使用越少的字节数。
     * 这能减少用来表示数字的字节数。 Varint 中的每个 byte 的最高位 bit 有特殊的含义，如果该位为 1，
     * 表示后续的 byte 也是该数字的一部分，如果该位为 0，则结束。其他的 7 个 bit 都用来表示数字。
     *
     * @param msgSize 消息体大小
     * @return protobuf header
     */
    private byte[] generateHeader(int msgSize) {
        int headerLen = computeRawVarint32Size(msgSize);
        byte[] headerBytes = new byte[headerLen];

        int i = 0;
        while (true) {
            if ((msgSize & ~0x7F) == 0) {
                headerBytes[i] = (byte) msgSize;
                return headerBytes;
            } else {
                headerBytes[i] = (byte) ((msgSize & 0x7F) | 0x80);
                msgSize >>>= 7;
                i++;
            }
        }
    }

    /**
     * Computes size of protobuf varint32 after encoding.
     *
     * @param value which is to be encoded.
     * @return size of value encoded as protobuf varint32.
     */
    private int computeRawVarint32Size(final int value) {
        if ((value & (0xffffffff << 7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffff << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffff << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffff << 28)) == 0) {
            return 4;
        }
        return 5;
    }

    /**
     * 解析出消息 body 字节数组
     *
     * @param data 带 header 的原始字节数组
     * @return
     */
    public byte[] decodeBytes(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }

        int[] headerInfo = readRawVarint32Header(data);
        int bodyLength = headerInfo[1];
        if (bodyLength <= 0) {
            return null;
        }
        int headLength = headerInfo[0];
        if (data.length - headLength < bodyLength) {
            // msg is corrupted
            return null;
        }

        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(data, headLength, bodyBytes, 0, bodyLength);

        return bodyBytes;
    }

    /**
     * Reads variable length 32bit int from buffer
     * <p>
     * Reference 1: io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder#readRawVarint32Header
     * Reference 2: https://www.cnblogs.com/tankaixiong/p/6366043.html
     * <p>
     * 从头开始读字节，如果字节第一位为 1， 则表示后续还有字节是表示消息头，
     * 当这个字节的第一位为 1，则这个字节肯定是负数（字节最高位表示正负），
     * 大于等于 0 表示描述消息体长度的头已经读完了。
     *
     * @return header info
     */
    private int[] readRawVarint32Header(byte[] buffer) {
        int varintByteCount = 0;// 头占用字节数
        int varintValue = 0;// 头中记载的消息体长度

        byte tmp = buffer[0];
        if (tmp >= 0) {
            varintByteCount = 1;
            varintValue = tmp;
        } else {
            varintValue = tmp & 127;
            if ((tmp = buffer[1]) >= 0) {
                varintByteCount = 2;
                varintValue |= tmp << 7;
            } else {
                varintValue |= (tmp & 127) << 7;
                if ((tmp = buffer[2]) >= 0) {
                    varintByteCount = 3;
                    varintValue |= tmp << 14;
                } else {
                    varintValue |= (tmp & 127) << 14;
                    if ((tmp = buffer[3]) >= 0) {
                        varintByteCount = 4;
                        varintValue |= tmp << 21;
                    } else {
                        varintValue |= (tmp & 127) << 21;
                        if ((tmp = buffer[4]) >= 0) {
                            varintByteCount = 5;
                            varintValue |= tmp << 28;
                        }
                    }
                }
            }
        }

        return new int[]{varintByteCount, varintValue};
    }

}
