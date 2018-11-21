package com.wtz.tools.encrypt2;


import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAEncrypt {

    private RSAPublicKey publicKey;

    public RSAEncrypt(byte[] publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(Constant.ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(Constant.TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            return RSAUtil.rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(Constant.CHARSET), publicKey.getModulus().bitLength());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
