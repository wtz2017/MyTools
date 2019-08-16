package com.wtz.tools.utils.network.socket.codec.impl;

import android.os.Build;


import com.wtz.tools.utils.network.socket.codec.ICodec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class StringCodec extends BaseCodec implements ICodec<String> {

    private final Charset charset;

    public StringCodec() {
        this(Charset.defaultCharset());
    }

    public StringCodec(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    public byte[] encode(String data) {
        if (data == null) {
            return new byte[0];
        }

        return encodeBytes(data.getBytes(charset));
    }

    @Override
    public String decode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        byte[] bodyBytes = decodeBytes(data);
        String result = null;
        // 从 android 6.0 开始，不支持直接 new String(byte bytes[], Charset charset)
        // 6.0 以上使用隐藏类 StringFactory 代替.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            result = new String(bodyBytes, charset);
        } else {
            try {
                Class<?> clazz = Class.forName("java.lang.StringFactory");
                Method m = clazz.getDeclaredMethod("newStringFromBytes",
                        new Class[] {byte[].class, Charset.class});
                m.setAccessible(true);
                result = (String) m.invoke(null, bodyBytes, charset);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
