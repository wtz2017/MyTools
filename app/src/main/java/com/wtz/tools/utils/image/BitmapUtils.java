package com.wtz.tools.utils.image;

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
import android.graphics.PorterDuff;
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
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
            opts.inPreferredConfig = Config.ARGB_8888;
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
            opts.inPreferredConfig = Config.ARGB_8888;
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
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
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
            bmp.compress(CompressFormat.PNG, 100, fops);
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

    /**
     * 把图片缩小到指定大小范围内
     * @return
     */
    public static Bitmap scaleIntoSizeRange(Bitmap srcBitmap, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        int w = srcBitmap.getWidth();
        int h = srcBitmap.getHeight();
        if (w > maxWidth || h > maxHeight) {
            float sw = w / (float) maxWidth;
            float sh = h / (float) maxHeight;
            float scale = sw > sh ? sw : sh;
            int destW = (int) (w / scale);
            int destH = (int) (h / scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, destW, destH, true);
        }
        return bitmap;
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
    public static int getBytesPerPixel(Config config) {
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
        Config config = Config.ARGB_8888;
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
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
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
        return Bitmap.createBitmap(cutPixels, 0, cutW, cutW, cutH, Config.ARGB_8888);
    }

    /**
     * 模糊图片的具体方法（API>=17）
     *
     * @param context 上下文对象
     * @param image   需要模糊的图片
     * @return 模糊处理后的图片
     */
    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return image;
        }

        // 计算图片缩小后的长宽
        int width = Math.round(image.getWidth() * 0.4f);
        int height = Math.round(image.getHeight() * 0.4f);

        // 将缩小后的图片做为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(blurRadius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public enum RoundCornerType {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM,
        LEFT, TOP, RIGHT, BOTTOM, ALL
    }

    /**
     * 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
     * 然后在画板上画出一个想要的形状的区域，通过绘制多个区域的并集达到目标形状
     * 最后把源图片贴上
     *
     * @param bitmap  原图
     * @param type    哪个角需要做成圆角
     * @param roundPx 圆角大小
     * @return
     */
    public static Bitmap roundImageCorner(Bitmap bitmap, RoundCornerType type, int roundPx) {
        try {
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();

            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(paintingBoard);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);

            switch (type) {
                case LEFT_TOP:
                    clipLeftTop(canvas, paint, roundPx, width, height);
                    break;
                case RIGHT_TOP:
                    clipRightTop(canvas, paint, roundPx, width, height);
                    break;
                case LEFT_BOTTOM:
                    clipLeftBottom(canvas, paint, roundPx, width, height);
                    break;
                case RIGHT_BOTTOM:
                    clipRightBottom(canvas, paint, roundPx, width, height);
                    break;
                case LEFT:
                    clipLeft(canvas, paint, roundPx, width, height);
                    break;
                case RIGHT:
                    clipRight(canvas, paint, roundPx, width, height);
                    break;
                case TOP:
                    clipTop(canvas, paint, roundPx, width, height);
                    break;
                case BOTTOM:
                    clipBottom(canvas, paint, roundPx, width, height);
                    break;
                case ALL:
                    clipAll(canvas, paint, roundPx, width, height);
                    break;
            }

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, width, height);
            final Rect dst = src;
            canvas.drawBitmap(bitmap, src, dst, paint);
            return paintingBoard;
        } catch (Exception e) {
            return bitmap;
        }
    }

    private static void clipLeftTop(final Canvas canvas, final Paint paint, int roundPx, int width, int height) {
        final Rect rect1 = new Rect(roundPx, 0, width, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(0, 0, roundPx * 2, height);
        canvas.drawRoundRect(rect2, roundPx, roundPx, paint);
        final Rect rect3 = new Rect(0, height - roundPx, roundPx, height);
        canvas.drawRect(rect3, paint);
    }

    private static void clipRightTop(final Canvas canvas, final Paint paint, int roundPx, int width, int height) {
        final Rect rect1 = new Rect(0, 0, width - roundPx, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(width - 2 * roundPx, 0, width, height);
        canvas.drawRoundRect(rect2, roundPx, roundPx, paint);
        final Rect rect3 = new Rect(width - roundPx, height - roundPx, width, height);
        canvas.drawRect(rect3, paint);
    }

    private static void clipLeftBottom(final Canvas canvas, final Paint paint, int roundPx, int width, int height) {
        final Rect rect1 = new Rect(roundPx, 0, width, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(0, 0, roundPx * 2, height);
        canvas.drawRoundRect(rect2, roundPx, roundPx, paint);
        final Rect rect3 = new Rect(0, 0, roundPx, roundPx);
        canvas.drawRect(rect3, paint);
    }

    private static void clipRightBottom(final Canvas canvas, final Paint paint, int roundPx, int width, int height) {
        final Rect rect1 = new Rect(0, 0, width - roundPx, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(width - 2 * roundPx, 0, width, height);
        canvas.drawRoundRect(rect2, roundPx, roundPx, paint);
        final Rect rect3 = new Rect(width - roundPx, 0, width, roundPx);
        canvas.drawRect(rect3, paint);
    }

    private static void clipLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect rect1 = new Rect(offset, 0, width, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(0, 0, offset * 2, height);
        canvas.drawRoundRect(rect2, offset, offset, paint);
    }

    private static void clipRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect rect1 = new Rect(0, 0, width - offset, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(width - offset * 2, 0, width, height);
        canvas.drawRoundRect(rect2, offset, offset, paint);
    }

    private static void clipTop(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect rect1 = new Rect(0, offset, width, height);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(0, 0, width, offset * 2);
        canvas.drawRoundRect(rect2, offset, offset, paint);
    }

    private static void clipBottom(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect rect1 = new Rect(0, 0, width, height - offset);
        canvas.drawRect(rect1, paint);
        final RectF rect2 = new RectF(0, height - offset * 2, width, height);
        canvas.drawRoundRect(rect2, offset, offset, paint);
    }

    private static void clipAll(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

}
