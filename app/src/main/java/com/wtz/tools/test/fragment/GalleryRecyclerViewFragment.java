package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ryan.rv_gallery.AnimManager;
import com.ryan.rv_gallery.GalleryRecyclerView;
import com.wtz.tools.R;
import com.wtz.tools.utils.image.BitmapUtils;
import com.wtz.tools.utils.image.GlideTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用RecyclerView做一个小清新的Gallery效果
 *
 * 源码：https://github.com/ryanlijianchang/Recyclerview-Gallery
 * 原理：https://juejin.im/post/5a30fe5a6fb9a045132ab1bf
 *
 * 使用注意：item宽高需要设置为：match_parent
 */
public class GalleryRecyclerViewFragment extends Fragment implements GalleryRecyclerView.OnItemClickListener {
    private static final String TAG = GalleryRecyclerViewFragment.class.getSimpleName();

    private View mContainer;
    private GalleryRecyclerView mRecyclerView1;
    private GalleryRecyclerView mRecyclerView2;

    private List<String> mImageList = new ArrayList<>();

    /**
     * 获取虚化背景的位置
     */
    private int mLastDraPosition = -1;
    private Map<String, Drawable> mTSDraCacheMap = new HashMap<>();
    private static final String KEY_PRE_DRAW = "key_pre_draw";

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=9b867a04b299a9012f38537575fc600e/4d086e061d950a7b86bee8d400d162d9f2d3c913.jpg");
        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=16f6ff0030292df583cea456d4583615/e1fe9925bc315c60b6b051c087b1cb13495477f3.jpg");
        mImageList.add("http://img.pptjia.com/image/20180117/f4b76385a3ccdbac48893cc6418806d5.jpg");
        mImageList.add("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1555409977&di=1cfd95dd9a6f6459de52ecf42ef50e0c&src=http://pic1.win4000.com/wallpaper/a/59a4d26aa053f.jpg");
        mImageList.add("http://imgsrc.baidu.com/imgad/pic/item/0bd162d9f2d3572c25e340088013632763d0c3e5.jpg");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_gallery_recycler_view, container, false);

        mContainer = view.findViewById(R.id.root);

        mRecyclerView1 = view.findViewById(R.id.rv_list1);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        // 背景高斯模糊 & 淡入淡出
        mRecyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    setBlurImage(false, mRecyclerView1);
                }
            }
        });
        setBlurImage(false, mRecyclerView1);

        mRecyclerView2 = view.findViewById(R.id.rv_list2);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        MyAdapter adapter = new MyAdapter(mImageList);
        mRecyclerView1.setAdapter(adapter);
        mRecyclerView2.setAdapter(adapter);

        setUp(mRecyclerView1);
        setUp(mRecyclerView2);

        return view;
    }

    private void setUp(GalleryRecyclerView galleryRecyclerView) {
        galleryRecyclerView
                // set scroll speed（pixel/s）
                .initFlingSpeed(9000)
                // set page distance and visible distance of the nearby.
                .initPageParams(0, 40)
                // set the animation factor
                .setAnimFactor(0.1f)
                // set animation type. you can choose AnimManager.ANIM_BOTTOM_TO_TOP or AnimManager.ANIM_TOP_TO_BOTTOM
                .setAnimType(AnimManager.ANIM_BOTTOM_TO_TOP)
                // set click listener
                .setOnItemClickListener(this)
                // set whether auto play
                .autoPlay(false)
                // set auto play intervel
                .intervalTime(2000)
                // set default position
                .initPosition(1)
                // finally call method
                .setUp();
    }

    /**
     * 设置背景高斯模糊
     */
    public void setBlurImage(boolean forceUpdate, final GalleryRecyclerView recyclerView) {
        MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
        final int mCurViewPosition = recyclerView.getScrolledPosition();

        boolean isSamePosAndNotUpdate = (mCurViewPosition == mLastDraPosition) && !forceUpdate;
        if (isSamePosAndNotUpdate) {
            return;
        }

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // 获取当前位置的图片
                MyAdapter.RecyclerViewHolder holder = ((MyAdapter) recyclerView.getAdapter()).getHolder(mCurViewPosition);
                Drawable drawable = holder.imageView.getDrawable();
                Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable);

                // 将该Bitmap高斯模糊后返回到resBlurBmp
                Bitmap resBlurBmp = BitmapUtils.blurBitmap(getActivity(), bitmap, 15f);
                // 再将resBlurBmp转为Drawable
                Drawable resBlurDrawable = new BitmapDrawable(resBlurBmp);
                // 获取前一页的Drawable
                Drawable preBlurDrawable = mTSDraCacheMap.get(KEY_PRE_DRAW) == null ? resBlurDrawable : mTSDraCacheMap.get(KEY_PRE_DRAW);

                /* 以下为淡入淡出效果 */
                Drawable[] drawableArr = {preBlurDrawable, resBlurDrawable};
                TransitionDrawable transitionDrawable = new TransitionDrawable(drawableArr);
                mContainer.setBackgroundDrawable(transitionDrawable);
                transitionDrawable.startTransition(500);

                // 存入到cache中
                mTSDraCacheMap.put(KEY_PRE_DRAW, resBlurDrawable);
                // 记录上一次高斯模糊的位置
                mLastDraPosition = mCurViewPosition;
            }
        });
    }

    @Override
    public void onItemClick(View view, int i) {
        Log.d(TAG, "GalleryRecyclerView onItemClick:" + i);
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
        if (mRecyclerView1 != null) {
            mRecyclerView1.release();
            mRecyclerView1 = null;
        }
        if (mRecyclerView2 != null) {
            mRecyclerView2.release();
            mRecyclerView2 = null;
        }
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

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Map<Integer, MyAdapter.RecyclerViewHolder> holderMap = new HashMap<>();

        private List<String> dataList;
        private RequestOptions options;

        public MyAdapter(List<String> dataList) {
            this.dataList = dataList;

            RoundedCorners roundedCorners = new RoundedCorners(50);
            options = RequestOptions.bitmapTransform(roundedCorners);
            options = GlideTool.addDefaultOptions(options);
        }

        public void updateData(List<String> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        public MyAdapter.RecyclerViewHolder getHolder(int position) {
            return holderMap.get(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder viewType:" + viewType);
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycler_gallery, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder position:" + position);
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            holderMap.put(position, recyclerViewHolder);
            Glide.with(GalleryRecyclerViewFragment.this)
                    .load(dataList.get(position))
                    .apply(options)
                    .listener(GlideTool.getDefaultDrawableListener(TAG))
                    .into(recyclerViewHolder.imageView);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            RecyclerViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.iv_img);
            }
        }
    }

}
