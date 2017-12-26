package com.wtz.tools.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.View;

public class BitmapUtils {
    private final static String TAG = BitmapUtils.class.getSimpleName();
    
    public static Bitmap getBitmapFromResource(Context context, int resId, int width, int height) {
        Bitmap scaled = null;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
            if (scaled != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
            scaled = bitmap;
        }
        return scaled;
    }

    /**
     * @param filePath
     *            文件路径
     * @param width
     *            实际需要展示的宽
     * @param height
     *            实际需要展示的高
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int width, int height) {
    
        BitmapFactory.Options opts = null;
        if (width > 0 && height > 0) {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            // 计算图片缩放比例
            final int minSideLength = Math.min(width, height);
            opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }
        try {
            return BitmapFactory.decodeFile(filePath, opts);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();
    
            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);
    
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, quality, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] img, int targetWidth, int targetHeight, String savePath) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(img, 0, img.length, opts);
            Log.d(TAG, "opts.outWidth = " + opts.outWidth + ", opts.outHeight = " + opts.outHeight);
    
            opts.inJustDecodeBounds = false;
            final int minSideLength = Math.min(targetWidth, targetHeight);
            opts.inSampleSize = BitmapUtils.computeSampleSize(opts, minSideLength, targetWidth * targetHeight);
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 有时使用decodeByteArray会失败：SkImageDecoder::Factory returned null，数据应该有问题
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
    
            // Bitmap bmp = Bitmap.createBitmap(1920, 1080, Bitmap.Config.RGB_565);
            // ByteBuffer srcbuffer = ByteBuffer.wrap(img);
            // bmp.copyPixelsFromBuffer(srcbuffer);
    
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static void bitmapToFile(Bitmap bmp, String path, boolean recycle) {
        FileOutputStream fops = null;
        try {
            fops = new FileOutputStream(new File(path));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fops);
            fops.flush();
            if (recycle) {
                bmp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fops != null) {
                try {
                    fops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param img
     *            图片字节数据流
     * @param targetWidth
     *            实际保存的分辨宽
     * @param targetHeight
     *            实际保存的分辨高
     * @param savePath
     *            文件保存的路径名称
     */
    public static void bytesToFile(byte[] img, int targetWidth, int targetHeight,
            String savePath) {
        Bitmap bmp = bytesToBitmap(img, targetWidth, targetHeight, savePath);
        bitmapToFile(bmp, savePath, true);
    }

    public static Bitmap scaleBitmap(Bitmap srcBitmap, int dstWidth, int dstHeight) {
        Bitmap scaled = null;
        try {
            scaled = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
            if (scaled != srcBitmap && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
            scaled = srcBitmap;
        }
        return scaled;
    }

    public static Bitmap rotateBitmap(Bitmap source, float degrees, boolean recycle) {
        if (degrees == 0) return source;
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(degrees);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
        if (recycle) source.recycle();
        return bitmap;
    }

    /**
     * 获得圆角图片
     * @param bitmap
     * @param roundPx 圆角大小
     * @return
     */
    public static Bitmap roundBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    
    /**
     * 获得带倒影的图片
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap reflectbitmap(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                h / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }
    
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     * @param config The bitmap configuration.
     * @return The byte usage per pixel.
     */
    public static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
    
    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).
     *
     * @param value
     * @return size in bytes
     */
    @TargetApi(VERSION_CODES.KITKAT)
    public static int getBitmapByteSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
    public static Bitmap takeScreenshot(View view) {
        assert view.getWidth() > 0 && view.getHeight() > 0;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), config);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
    
    /**
     * @param colorString
     *            Supported formats are: •#RRGGBB •#AARRGGBB
     * 
     * @param radius
     *            The radius in pixels of the corners of the rectangle shape
     * @return
     */
    public static Drawable getShapeDrawable(String colorString, float radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor(colorString));
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }
    
    /**
     * 渐变效果图，
     *
     * @param context
     * @param resource
     * @param centerX
     *            渐变开始的地方（0~1）
     * @param endX
     *            渐变结束地地方（0~1，>centerX）
     * @return
     */
    public static Bitmap gradient(Bitmap resource, float centerX, float endX) {
        int height = resource.getHeight();
        int width = resource.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int[] resPixels = new int[width * height];
        int[] srcPixels = new int[width * height];
        resource.getPixels(resPixels, 0, width, 0, 0, width, height);
        int end = (int) (width * endX);
        int center = (int) (width * centerX);
        int pos, pixColor, pixR, pixG, pixB, pixA;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pos = i * width + j;
                if (j < center) {
                    srcPixels[pos] = Color.argb(0, 0, 0, 0);
                } else if (j >= center && j < end) {
                    pixColor = resPixels[pos];
                    pixR = Color.red(pixColor);
                    pixG = Color.green(pixColor);
                    pixB = Color.blue(pixColor);
                    pixA = (int) (1.0 * Color.alpha(pixColor) * (j - center) / (end - center));
                    srcPixels[pos] = Color.argb(pixA, pixR, pixG, pixB);
                } else {
                    srcPixels[pos] = resPixels[pos];
                }
            }
        }
        bitmap.setPixels(srcPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    
    public static Bitmap cutBitmap(Bitmap bmpSrc, int cutX, int cutY, int cutW, int cutH) {
        if (bmpSrc == null || cutX < 0 || cutY < 0 || cutW <= 0 || cutH <= 0) {
            return null;
        }
        if (cutX + cutW > bmpSrc.getWidth() || cutY + cutH > bmpSrc.getHeight()) {
            return null;
        }
        int[] cutPixels = new int[cutW * cutH];
        bmpSrc.getPixels(cutPixels, 0, cutW, cutX, cutY, cutW, cutH);
        // If the config does not support per-pixel alpha (e.g. RGB_565), 
        // then the alpha bytes in the colors[] will be ignored (assumed to be FF)
        return Bitmap.createBitmap(cutPixels, 0, cutW, cutW, cutH, Bitmap.Config.ARGB_8888);
    }
}
