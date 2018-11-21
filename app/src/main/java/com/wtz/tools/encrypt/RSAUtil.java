package com.wtz.tools.encrypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class RSAUtil {
    private static String PUBLIC_KEY_BASE64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcJUcW07oJgA2/P1f9mTFvxsxeYa/nk48viy1VPbCSL9JuJHd6ZDVucefDVI1RpzVZtT7taGlDjY9UI9sJDNNqfJzbgGgRh4udgGc+CFzc4DqYC4prU4a5loZwvpk7htw6BYzRskpfUzezrHYYVkNfT6OWpanWtvpMzgMdkqG3pwIDAQAB";
    private static String PRIVATE_KEY_BASE64 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANwlRxbTugmADb8/V/2ZMW/GzF5hr+eTjy+LLVU9sJIv0m4kd3pkNW5x58NUjVGnNVm1Pu1oaUONj1Qj2wkM02p8nNuAaBGHi52AZz4IXNzgOpgLimtThrmWhnC+mTuG3DoFjNGySl9TN7OsdhhWQ19Po5alqda2+kzOAx2SobenAgMBAAECgYEAmzzWQmyPNX/NMd1zKOtMByxClROBvWU6hSY5k3BrAMskMnMYpX/ghb9g1UbcWIX2gwniE/uarv414flezaTzt0bSVxRsvhy56B/8hzXRWhd1CN7PgQ1Jgw2MQjYf8fo75s7T7ZNRsDHT88NJjVJHpm/UPgqLkCmpq8Scjz5/yBECQQDvN4OMPlahL3WE9hNxYaUAzAL3VKqAkarEnX+jioLx7iUgUU0goTlrNiJeh9tl7o58ZL6SNnXz+Uq++tmxu9JtAkEA65c7fON8poX2lP3tnBA8REE2Y3SJvxnLKpbWZHF3dYLMhikRurZNKVZpeuGo/f9OCq41td6Vom7TP7M2maIF4wJACz4IqKalW53nYABQmZuFLaxo10bbXH98DxF31i/ER6pxhDtN7KKnGCrLIrYv0XKMr7vsWOLGWFj28lLHuFqjYQJBAKPJ6Z7DHXc8bZlaDObSVsPxPpnYfKeyjBAY/Aaq6C13eS/Qv7W6ZlzV6dF3r6Ce7af5Q/gkPo3GVPHVa6FWBpcCQEPCcHjAowVjKN0nBYPnLqt1jHChGZKXSn1QMUWn3XpBqDnQKRj7UnmBYLhjewHrBD85uJjvKQ9Nqcc3265r1Z8=";

    private static String ENCODE = "UTF-8";

    public static String[] generalKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 密钥位数
        keyPairGen.initialize(1024);
        // 密钥对
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new String[] {
                Base64.encodeBytes(publicKey.getEncoded()),
                Base64.encodeBytes(privateKey.getEncoded())
        };
    }

    public static String encrypt(String content) throws Exception {
        byte[] publicKeyBytes = Base64.decode(PUBLIC_KEY_BASE64);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] enBytes = cipher.doFinal(content.getBytes(ENCODE));
        String source = Base64.encodeBytes(enBytes);
        return source;
    }

    public static String decrypt(String content) throws Exception {
        byte[] privateKeyBytes = Base64.decode(PRIVATE_KEY_BASE64);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] deBytes = cipher.doFinal(Base64.decode(content));
        //对于有些加密原字串提前做了其它加密，则解密时还须对应先解密，比如SDK中可能对参数串进行URLDecoder.decode，则应该用以下方式解密：
        //byte[] deBytes = cipher.doFinal(Base64.decode(URLDecoder.decode(content, ENCODE)));

        return new String(deBytes, ENCODE);
    }

}
