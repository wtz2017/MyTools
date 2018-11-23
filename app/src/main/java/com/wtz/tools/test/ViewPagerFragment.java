package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wtz.tools.R;
import com.wtz.tools.test.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerFragment extends Fragment {
    private static final String TAG = ViewPagerFragment.class.getSimpleName();

    private List<String> mImageList = new ArrayList<>();
    private LinearLayout mOpDescIndexLayout;
    private ViewPager mOpViewPager;
    private int mPageIndex;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        initImageLoader(getActivity());

        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=9b867a04b299a9012f38537575fc600e/4d086e061d950a7b86bee8d400d162d9f2d3c913.jpg");
        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=16f6ff0030292df583cea456d4583615/e1fe9925bc315c60b6b051c087b1cb13495477f3.jpg");
        mImageList.add("http://img.pptjia.com/image/20180117/f4b76385a3ccdbac48893cc6418806d5.jpg");
        mImageList.add("http://pic.qiantucdn.com/58pic/25/99/58/58aa038a167e4_1024.jpg");
        mImageList.add("http://imgsrc.baidu.com/imgad/pic/item/0bd162d9f2d3572c25e340088013632763d0c3e5.jpg");
    }

    private void initImageLoader(Context context) {
        if (ImageLoader.getInstance().isInited()) {
            return;
        }

        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultDisplayImageOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        // 初始化图片切换索引圆点
        int size = mImageList.size();
        mOpDescIndexLayout = (LinearLayout) view.findViewById(R.id.img_index_layout);
        final List<View> pointViewList = new ArrayList<View>();
        mOpDescIndexLayout.removeAllViews();
        for (int i = 0; i < size; i++) {
            View point = inflater.inflate(R.layout.item_img_index_point, mOpDescIndexLayout, false);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.img_index_select);
                point.setScaleX(1.267f);
                point.setScaleY(1.267f);
            } else {
                point.setBackgroundResource(R.drawable.img_index_unselect);
            }
            mOpDescIndexLayout.addView(point);
            pointViewList.add(point);
        }

        // 初始化图片切换页面
        mOpViewPager = (ViewPager) view.findViewById(R.id.vp_imgs);
        DisplayImageOptions imageOptions = getImgOptions();
        List<ImageView> imageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < size; i++) {
            ImageLoadingListener loadImgListener = getLoadImgListener();
            ImageView iv = new ImageView(getActivity());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoader.getInstance().displayImage(mImageList.get(i), iv, imageOptions, loadImgListener);
            imageViewList.add(iv);
        }
        mOpViewPager.setAdapter(new ViewPagerAdapter(imageViewList));
        mOpViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected...position = " + position + ", last = " + mPageIndex);
                int last = mPageIndex;
                mPageIndex = position;
                View lastPoint = pointViewList.get(last);
                lastPoint.setBackgroundResource(R.drawable.img_index_unselect);
                lastPoint.setScaleX(1f);
                lastPoint.setScaleY(1f);

                View currPoint = pointViewList.get(position);
                currPoint.setBackgroundResource(R.drawable.img_index_select);
                currPoint.setScaleX(1.267f);
                currPoint.setScaleY(1.267f);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        mOpViewPager.setCurrentItem(mPageIndex);

        return view;
    }

    private DisplayImageOptions getImgOptions() {
        return new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
    }

    private ImageLoadingListener getLoadImgListener() {
        return new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String url, View view) {
                Log.d(TAG, "ImageLoader--->onLoadingStarted, imageUri = " + url);
            }

            @Override
            public void onLoadingFailed(String url, View view, FailReason failReason) {
                Log.d(TAG, "ImageLoader--->onLoadingFailed, imageUri = " + url);
            }

            @Override
            public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                Log.d(TAG, "ImageLoader--->onLoadingComplete, imageUri = " + url);
            }

            @Override
            public void onLoadingCancelled(String url, View view) {
                Log.d(TAG, "ImageLoader--->onLoadingCancelled, imageUri = " + url);
            }
        };
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
