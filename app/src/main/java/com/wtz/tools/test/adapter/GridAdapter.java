package com.wtz.tools.test.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wtz.tools.R;

import java.util.List;


public class GridAdapter extends BaseAdapter {
    private final static String TAG = GridAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mDataList;
    private DisplayImageOptions mImageOptions;

    public GridAdapter(Context context, List<String> dataList) {
        mContext = context;
        mDataList = dataList;
        initImageLoader(context);
        mImageOptions = getImgOptions();
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

    private DisplayImageOptions getImgOptions() {
        return new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).showImageOnLoading(R.drawable.image_default)
                .showImageForEmptyUri(R.drawable.image_default).showImageOnFail(R.drawable.image_default)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
    }

    public void updateData(List<String> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mDataList == null) ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return (mDataList == null) ? null : mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, parent,
                    false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoadingListener loadImgListener = getLoadImgListener();
        ImageLoader.getInstance().displayImage((String) getItem(position), holder.imageView, mImageOptions, loadImgListener);

        return convertView;
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

    class ViewHolder {
        ImageView imageView;
    }

}