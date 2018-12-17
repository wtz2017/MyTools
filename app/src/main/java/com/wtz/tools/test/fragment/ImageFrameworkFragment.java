package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.wtz.tools.R;
import com.wtz.tools.utils.image.FrescoTool;
import com.wtz.tools.utils.image.GlideTool;
import com.wtz.tools.utils.image.UILTool;

public class ImageFrameworkFragment extends Fragment {
    private static final String TAG = ImageFrameworkFragment.class.getSimpleName();

    ImageView ivUIL;

    ImageView ivPicasso;

    ImageView ivGlide;
    ImageView ivGlideWebp;
    ImageView ivGlideGif;

    SimpleDraweeView ivFresco;
    SimpleDraweeView ivFrescoWebp;
    SimpleDraweeView ivFrescoGif;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        testCotentProvider();

        Fresco.initialize(getActivity());//至少在view加载前初始化
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_image_framework, container, false);

        ivUIL = view.findViewById(R.id.iv_uil);
        ivPicasso = view.findViewById(R.id.iv_picasso);
        ivGlide = view.findViewById(R.id.iv_glide);
        ivGlideWebp = view.findViewById(R.id.iv_glide_webp);
        ivGlideGif = view.findViewById(R.id.iv_glide_gif);
        ivFresco = view.findViewById(R.id.iv_fresco);
        ivFrescoWebp = view.findViewById(R.id.iv_fresco_webp);
        ivFrescoGif = view.findViewById(R.id.iv_fresco_gif);

        testUIL();
        testPicasso();
        testGlide();
        testFresco();

        return view;
    }

    /**
     * UIL Acceptable URIs examples:
     * "http://site.com/image.png" // from Web
     * "file:///mnt/sdcard/image.png" // from SD card
     * "file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
     * "content://media/external/images/media/13" // from content provider
     * "content://media/external/video/media/13" // from content provider (video thumbnail)
     * "assets://image.png" // from assets
     * "drawable://" + R.drawable.img // from drawables (non-9patch images)
     */
    private void testUIL() {
        String url = "http://pic.58pic.com/58pic/13/86/80/95h58PIC5jK_1024.jpg";
//        String url = UILTool.assetPath("hong_xia.webp");
        UILTool.init(getActivity());
        ImageLoader.getInstance().displayImage(url, ivUIL,
                UILTool.getDefaultOptions(), UILTool.getDefaultListener(TAG));
    }

    private void testPicasso() {
        String url = "http://pic41.nipic.com/20140601/18681759_143805185000_2.jpg";
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .rotate(45f)
                .into(ivPicasso);
    }

    /**
     * "file://"+ Environment.getExternalStorageDirectory().getPath()+"/test.jpg"
     * "file:///android_asset/f003.gif
     * "android.resource://com.frank.glide/raw/"+R.raw.raw_1
     * 以下两种直接传递对应的uri即可
     * "content://media/external/images/media/141294"
     * "http://pic24.photophoto.cn/20120923/0017030054071515_b.jpg"
     */
    private void testGlide() {
//        String url = "http://pic24.photophoto.cn/20120923/0017030054071515_b.jpg";
        String url = GlideTool.rawPath(getActivity(), R.raw.raw1);
        String webp = GlideTool.assetPath("hong_xia.webp");
        String gif = "http://img.soogif.com/iUFnaMML9KxVven3A7h1UpeLK9iXkRTT.gif";

        RequestOptions options = RequestOptions.circleCropTransform();//圆形图片
        options = GlideTool.addDefaultOptions(options);
        Glide.with(this)
                .load(url)
                .apply(options)
                .listener(GlideTool.getDefaultDrawableListener(TAG))
                .into(ivGlide);

        RoundedCorners roundedCorners = new RoundedCorners(50);
        RequestOptions options2 = RequestOptions.bitmapTransform(roundedCorners);
        options2 = GlideTool.addDefaultOptions(options2);
        Glide.with(this)
                .load(webp)
                .apply(options2)
                .listener(GlideTool.getDefaultDrawableListener(TAG))
                .into(ivGlideWebp);

        RequestOptions options3 = RequestOptions.noTransformation();
        options3 = GlideTool.addDefaultOptions(options3);
        Glide.with(this)
                .asGif()
                .load(gif)
                .apply(options3)
                .listener(GlideTool.getDefaultGifListener(TAG))
                .into(ivGlideGif);
    }

    private Uri testCotentProvider() {
        String picPath = "/storage/emulated/0/DCIM/Camera/IMG_20160402_122613.jpg";
        Uri mUri = Uri.parse("content://media/external/images/media");
        Uri mImageUri = null;

        Cursor cursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String data = cursor.getString(cursor
                    .getColumnIndex(MediaStore.MediaColumns.DATA));
            if (picPath.equals(data)) {
                int ringtoneID = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                mImageUri = Uri.withAppendedPath(mUri, ""
                        + ringtoneID);
                break;
            }
            cursor.moveToNext();
        }
        Log.d(TAG, "testCotentProvider:" + ((mImageUri != null) ? mImageUri.toString() : ""));
        return mImageUri;
    }

    private void testFresco() {
        String url = "http://pic18.photophoto.cn/20110106/0020032817703440_b.jpg";
        //String url = FrescoTool.rawPath(getActivity(), R.raw.raw1);
        String webp = FrescoTool.assetPath("tie_ta.webp");
        String gif = "http://img.soogif.com/4Ve6FYYodhIO7jfy2ixRLefeMBjqOqEF.gif";

        //Fresco.initialize(getActivity());// TODO 这里调用已经晚了，至少要在view加载前初始化

        ivFresco.setHierarchy(FrescoTool.getDefaultHierarchy(getActivity()));
        ivFresco.setController(FrescoTool.getDefaultController(
                FrescoTool.getDefaultRequest(url),
                FrescoTool.getDefaultListener(TAG),
                ivFresco.getController()));

        ivFrescoWebp.setHierarchy(FrescoTool.getDefaultHierarchy(getActivity()));
        ivFrescoWebp.setController(FrescoTool.getDefaultController(
                FrescoTool.getDefaultRequest(webp),
                FrescoTool.getDefaultListener(TAG),
                ivFrescoWebp.getController()));

        ivFrescoGif.setHierarchy(FrescoTool.getDefaultHierarchy(getActivity()));
        ivFrescoGif.setController(FrescoTool.getDefaultController(
                FrescoTool.getDefaultRequest(gif),
                FrescoTool.getDefaultListener(TAG),
                ivFrescoGif.getController()));
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
