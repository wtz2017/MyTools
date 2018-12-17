package com.wtz.tools.utils.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.wtz.tools.R;

public class GlideTool {

    public static String filePath(String filePath) {
        return "file://" + filePath;
    }

    public static String assetPath(String assetFileName) {
        return "file:///android_asset/" + assetFileName;
    }

    public static String rawPath(Context context, int rawId) {
        return "android.resource://" + context.getPackageName() + "/raw/" + rawId;
    }

    public static RequestOptions addDefaultOptions(RequestOptions options) {
        return options.placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    public static RequestListener<Drawable> getDefaultDrawableListener(final String tag) {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d(tag, "glide onLoadFailed...model=" + model + ", e=" + e.toString());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d(tag, "glide onResourceReady...resource=" + resource + ", model=" + model + ", dataSource=" + dataSource + ", isFirstResource=" + isFirstResource);
                return false;
            }
        };
    }

    public static RequestListener<GifDrawable> getDefaultGifListener(final String tag) {
        return new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                Log.d(tag, "glide onLoadFailed...model=" + model + ", e=" + e.toString());
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d(tag, "glide onResourceReady...resource=" + resource + ", model=" + model + ", dataSource=" + dataSource + ", isFirstResource=" + isFirstResource);
                return false;
            }
        };
    }

}
