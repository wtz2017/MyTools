package com.wtz.tools.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wtz.tools.R;

public class UILTool {

    private static DisplayImageOptions sDisplayImageOptions;

    public static String filePath(String filePath) {
        return "file://" + filePath;
    }

    public static String assetPath(String assetFileName) {
        return "assets://" + assetFileName;
    }

    public static void init(Context context) {
        if (ImageLoader.getInstance().isInited()) {
            return;
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(getDefaultOptions())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getDefaultOptions() {
        if (sDisplayImageOptions == null) {
            sDisplayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .resetViewBeforeLoading(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new RoundedBitmapDisplayer(360))
                    .showImageOnLoading(R.drawable.image_default)
                    .showImageForEmptyUri(R.drawable.image_default)
                    .showImageOnFail(R.drawable.image_default)
                    .build();
        }
        return sDisplayImageOptions;
    }

    public static ImageLoadingListener getDefaultListener(final String tag) {
        return new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String url, View view) {
                Log.d(tag, "ImageLoader--->onLoadingStarted, imageUri = " + url);
            }

            @Override
            public void onLoadingFailed(String url, View view, FailReason failReason) {
                Log.d(tag, "ImageLoader--->onLoadingFailed, imageUri = " + url);
            }

            @Override
            public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                Log.d(tag, "ImageLoader--->onLoadingComplete, imageUri = " + url);
            }

            @Override
            public void onLoadingCancelled(String url, View view) {
                Log.d(tag, "ImageLoader--->onLoadingCancelled, imageUri = " + url);
            }
        };
    }

}
