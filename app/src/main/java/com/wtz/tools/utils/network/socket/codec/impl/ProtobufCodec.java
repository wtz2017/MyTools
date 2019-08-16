package com.wtz.tools.utils.network.socket.codec.impl;

import com.google.protobuf.AbstractMessageLite;
import com.wtz.tools.utils.network.socket.codec.ICodec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProtobufCodec<T extends AbstractMessageLite> extends BaseCodec implements ICodec<T> {
    private static final String TAG = "ProtobufCodec";
    private Class<T> typeClass;

    public ProtobufCodec(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public byte[] encode(T data) {
        if (data == null) {
            return new byte[0];
        }
        return encodeBytes(data.toByteArray());
    }

    @Override
    public T decode(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }

        byte[] bodyBytes = decodeBytes(data);
        try {
            Method m = typeClass.getDeclaredMethod("parseFrom", byte[].class);
            m.setAccessible(true);
            return (T) m.invoke(null, bodyBytes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
