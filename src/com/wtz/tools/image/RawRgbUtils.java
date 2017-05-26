package com.wtz.tools.image;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class RawRgbUtils {
    private final static String TAG = RawRgbUtils.class.getSimpleName();
    
    private void testRgb() {
        // 0xff换算到十进制就是-1
        byte[] rgb = new byte[] { 
                -1, -1, -1,/* 像素1 */
                -1, 0, 0,/* 像素2 */
                0, -1, 0,/* 像素3 */
                0, 0, -1 /* 像素4 */ 
                };
        Bitmap bmp = RawRgbUtils.decodeRGB888ToBitmap(rgb, 2, 2);

        File file = new File("/sdcard/test_rgb.png");
        FileOutputStream fops;
        try {
            fops = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fops);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testAbgr() {
        byte[] abgr = new byte[] { 
                -1, -1, -1, -1,
                -1, -1, 0, 0,
                -1, 0, -1, 0,
                -1, 0, 0, -1 };
        Bitmap bmp = RawRgbUtils.decodeABGR8888ToBitmap(abgr, 2, 2);

        File file = new File("/sdcard/test_abgr.png");
        FileOutputStream fops;
        try {
            fops = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fops);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeRGB888ToBitmap(byte[] rawRgb, int srcWidth, int srcHeight) {
        int[] colors = convertRGB888ByteToARGB8888Int(rawRgb);
        if (colors == null) {
            Log.d(TAG, "colors == null");
            return null;
        }
    
        Bitmap bmp = Bitmap.createBitmap(colors, 0, srcWidth, srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
    
        return bmp;
    }
    
    public static Bitmap decodeABGR8888ToBitmap(byte[] rawAbgr, int srcWidth, int srcHeight) {
        int[] colors = convertABGR8888ByteToARGB8888Int(rawAbgr);
        if (colors == null) {
            Log.d(TAG, "colors == null");
            return null;
        }
        
        Bitmap bmp = Bitmap.createBitmap(colors, 0, srcWidth, srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        
        return bmp;
    }

    private static int[] convertRGB888ByteToARGB8888Int(byte[] data) {
        int size = data.length;
        Log.d(TAG, "convertRGB888BytesToARGB8888Int...data.length = " + size);
        if (size == 0) {
            return null;
        }
    
        int arg = 0;
        if (size % 3 != 0) {
            arg = 1;
        }
        Log.d(TAG, "convertRGB888BytesToARGB8888Int...arg = " + arg);
    
        int[] color = new int[size / 3 + arg];
        int red, green, blue;
    
        for (int i = 0; i < color.length; ++i) {
            red = convertByteToInt(data[i * 3]);
            green = convertByteToInt(data[i * 3 + 1]);
            blue = convertByteToInt(data[i * 3 + 2]);
            
            color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
            Log.d(TAG, "convertRGB888BytesToARGB8888Int...color[" + i + "] = " + color[i]);
        }
        
        if (arg == 1) {
            color[color.length - 1] = 0xFF000000;
        }
    
        return color;
    }
    
    private static int[] convertABGR8888ByteToARGB8888Int(byte[] data) {
        int size = data.length;
        Log.d(TAG, "convertABGR8888ToARGB8888Int...data.length = " + size);
        if (size == 0) {
            return null;
        }
        
        int arg = 0;
        if (size % 4 != 0) {
            arg = 1;
        }
        Log.d(TAG, "convertABGR8888ToARGB8888Int...arg = " + arg);
        
        int[] color = new int[size / 4 + arg];
        int alpha, red, green, blue;
        
        for (int i = 0; i < color.length; ++i) {
            alpha = convertByteToInt(data[i * 4]);
            blue = convertByteToInt(data[i * 4 + 1]);
            green = convertByteToInt(data[i * 4 + 2]);
            red = convertByteToInt(data[i * 4 + 3]);
            
            color[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            Log.d(TAG, "convertABGR8888ToARGB8888Int...color[" + i + "] = " + color[i]);
        }
        
        if (arg == 1) {
            color[color.length - 1] = 0xFF000000;
        }
        
        return color;
    }

    private static int convertByteToInt(byte data) {

        int heightBit = (int) ((data >> 4) & 0x0F);
        int lowBit = (int) (0x0F & data);
        // 16 times said move left four bits, namely 4 power of 2
        return heightBit * 16 + lowBit;
    }
}
