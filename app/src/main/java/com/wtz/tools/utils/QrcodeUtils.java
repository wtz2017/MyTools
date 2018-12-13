package com.wtz.tools.utils;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QrcodeUtils {
    private final static String TAG = QrcodeUtils.class.getSimpleName();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Bitmap CreateQrCode(String content, int width, int height) throws WriterException {
        Log.d(TAG, "CreateQrCode...content = " + content);
        Log.d(TAG, "CreateQrCode...width=" + width);
        Log.d(TAG, "CreateQrCode...height=" + height);
        if (TextUtils.isEmpty(content) || width <=0 || height <= 0) {
            return null;
        }
        
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION,  ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        
        int[] rec = matrix.getEnclosingRectangle();// 获取二维码有效图案的属性
        Log.d(TAG, "CreateQrCode...Rectangle.length=" + rec.length);
        Log.d(TAG, "CreateQrCode...Rectangle.[0]=" + rec[0]);
        Log.d(TAG, "CreateQrCode...Rectangle.[1]=" + rec[1]);
        Log.d(TAG, "CreateQrCode...Rectangle.[2]=" + rec[2]);
        Log.d(TAG, "CreateQrCode...Rectangle.[3]=" + rec[3]);
        
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] pixels = new int[w * h];
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * w + x] = 0xff000000;
                } else {
                    pixels[y * w + x] = 0xffffffff;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
