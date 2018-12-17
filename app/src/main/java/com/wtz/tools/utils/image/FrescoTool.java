package com.wtz.tools.utils.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wtz.tools.R;

public class FrescoTool {

    public static String filePath(String filePath) {
        return "file://" + filePath;
    }

    public static String assetPath(String assetFileName) {
        return "asset:///" + assetFileName;
    }

    public static String rawPath(Context context, int rawId) {
        return "res://" + context.getPackageName() + "/" + rawId;
    }

    public static GenericDraweeHierarchy getDefaultHierarchy(Context context) {
        Resources resources = context.getResources();
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(resources);
        //设置的到图片的缩放类型
        hierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        //builder.setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
        //缩放类型为focusCrop时，需要设定一个居中点
        //(0f,0f)表示左上对齐显示，(1f,1f)表示右下对齐显示
        //PointF pf = new PointF(1f, 1f);
        //builder.setActualImageFocusPoint(pf);
        //进度条，占位图片消失，加载图片展现的时间间隔
        hierarchyBuilder.setFadeDuration(1000);
        //加载失败之后显示的图片及图片缩放类型
        hierarchyBuilder.setFailureImage(R.drawable.alert_circle, ScalingUtils.ScaleType.CENTER_INSIDE);
        //设置占位图片及缩放类型
        hierarchyBuilder.setPlaceholderImage(R.drawable.image_default, ScalingUtils.ScaleType.FIT_CENTER);
        //加载进度条图片及缩放类型
        AnimationDrawable animationDrawable = new AnimationDrawable();
        Drawable drawable = resources.getDrawable(R.drawable.loading_anim);
        animationDrawable.addFrame(drawable, 200);
        animationDrawable.setOneShot(false);
        hierarchyBuilder.setProgressBarImage(animationDrawable, ScalingUtils.ScaleType.CENTER_INSIDE);
        //提示重新加载的图片及缩放类型
        hierarchyBuilder.setRetryImage(R.mipmap.ic_launcher, ScalingUtils.ScaleType.CENTER_CROP);
        //设置背景图片
        hierarchyBuilder.setBackground(resources.getDrawable(R.color.image_bg_color));
        //在图片上方覆盖一个图片资源
        //builder.setOverlay(getResources().getDrawable(R.drawable.run));
        hierarchyBuilder.setPressedStateOverlay(resources.getDrawable(R.color.image_press_color));
        RoundingParams rp = new RoundingParams();
        //是否要将图片剪切成圆形
        rp.setRoundAsCircle(false);
        //设置哪个角需要变成圆角
        rp.setCornersRadii(100f, 0f, 100f, 0f);
        //圆角部分填充色
        rp.setOverlayColor(resources.getColor(R.color.image_bg_color));
        //边框宽度
        rp.setBorderWidth(20f);
        //边框填充色
        rp.setBorderColor(resources.getColor(R.color.image_border_color));
        hierarchyBuilder.setRoundingParams(rp);

        return hierarchyBuilder.build();
    }

    public static ImageRequest getDefaultRequest(String uriStr) {
        return ImageRequestBuilder
                //设置URI
                .newBuilderWithSource(Uri.parse(uriStr))
                //最低级别请求
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                //图片缩放
                //.setResizeOptions(new ResizeOptions(width, height))
                //渐进式加载，渐进式JPEG图仅仅支持网络图。本地图片会一次解码完成，所以没必要渐进式加载。你还需要知道的是，并不是所有的JPEG图片都是渐进式编码的
                .setProgressiveRenderingEnabled(false)
                .build();
    }

    public static ControllerListener getDefaultListener(final String tag) {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                //提交请求之前调用的方法
                Log.d(tag, "Fresco onSubmit: " + id);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                // 所有图片都加载成功时触发的方法
                Log.d(tag, "Fresco onFinalImageSet: " + id + ", imageInfo:" + imageInfo.getWidth() + "x" + imageInfo.getHeight());
            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                //当中间图片下载成功的时候触发，用于多图请求
                Log.d(tag, "Fresco onIntermediateImageSet: " + id);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                //当中间图片下载失败的时候触发，用于多图请求
                Log.d(tag, "Fresco onIntermediateImageFailed: " + id + "," + throwable.toString());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                // 加载图片失败时回调的方法
                Log.d(tag, "Fresco onFailure: " + id + "," + throwable.toString());
            }

            @Override
            public void onRelease(String id) {
                //释放图片资源时加载的方法
                Log.d(tag, "Fresco onRelease: " + id);
            }
        };
    }

    public static DraweeController getDefaultController(ImageRequest request,
                                                        ControllerListener listener,
                                                        DraweeController oldController) {
        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder();
        //设置uri
        //controllerBuilder.setUri(Uri.parse(url));
        controllerBuilder.setImageRequest(request);
        //设置监听
        controllerBuilder.setControllerListener(listener);
        //在指定一个新的controller的时候，使用setOldController，这可节省不必要的内存分配。
        controllerBuilder.setOldController(oldController);

        //设置加载图片完成后是否直接进行播放
        controllerBuilder.setAutoPlayAnimations(true);
        //加载失败之后，点击提示重新加载的图片资源重新加载
        controllerBuilder.setTapToRetryEnabled(true);

        return controllerBuilder.build();
    }

}
