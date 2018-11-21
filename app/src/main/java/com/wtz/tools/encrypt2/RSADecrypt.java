package com.wtz.tools.encrypt2;


import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class RSADecrypt {

    private RSAPrivateKey privateKey;

    public RSADecrypt(byte[] privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(Constant.ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(Constant.TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(RSAUtil.rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data), privateKey.getModulus().bitLength()), Constant.CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
