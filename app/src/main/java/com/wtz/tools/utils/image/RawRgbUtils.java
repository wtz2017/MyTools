package com.wtz.tools.utils.image;

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

        int pixelCount = size / 4;
        int[] color = new int[pixelCount];
        byte alpha, red, green, blue;

        for (int i = 0; i < pixelCount; ++i) {
            blue = data[i * 4];
            green = data[i * 4 + 1];
            red = data[i * 4 + 2];
            alpha = data[i * 4 + 3];

            // ARGB_8888 格式
            // int color = (A & 0xff) << 24 | (B & 0xff) << 16 | (G & 0xff) << 8 | (R & 0xff);
            color[i] = (alpha & 0xff) << 24 | (blue & 0xff) << 16 | (green & 0xff) << 8 | (red & 0xff);
        }
        return color;
    }

    private static int convertByteToInt(byte data) {

        int heightBit = (int) ((data >> 4) & 0x0F);
        int lowBit = (int) (0x0F & data);
        // 16 times said move left four bits, namely 4 power of 2
        return heightBit * 16 + lowBit;
    }

    public static Bitmap convertRgbToBitmap(byte[] imageData, int width, int height) throws IllegalArgumentException {
        int[] bmpData = new int[width * height];
        for (int i = 0; i < height; i += 1) {
            for (int j = 0; j < width; j += 1) {
                byte r = imageData[i * width * 3 + j * 3];
                byte g = imageData[i * width * 3 + j * 3 + 1];
                byte b = imageData[i * width * 3 + j * 3 + 2];
                int pixelVal = 0xFF000000;
                pixelVal |= (r << 16) & 0x00FF0000;
                pixelVal |= (g << 8) & 0x0000FF00;
                pixelVal |= (b) & 0x000000FF;
                bmpData[i * width + j] = pixelVal;
            }
        }
        Bitmap bm = Bitmap.createBitmap(bmpData, width, height, Bitmap.Config.ARGB_8888);
        return bm;
    }

}
