package com.wtz.tools.animation;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.wtz.tools.R;

public class FrameAnimDemo {

    private final static String TAG = "FrameAnimDemo";

    private ImageView mFrameView;
    private AnimationDrawable mAnimDrawable;
    private int mAnimDuration = 0;
    private Handler mHandler;

    public void initFrameAnim(ImageView view, Handler handler) {
        mHandler = handler;
        mFrameView = view;

        mFrameView.setBackgroundResource(R.drawable.frame_anim_list);
        mAnimDrawable = (AnimationDrawable) mFrameView.getBackground();
        for (int i = 0; i < mAnimDrawable.getNumberOfFrames(); i++) {
            mAnimDuration += mAnimDrawable.getDuration(i);
        }
    }

    public void startFrameAnim() {
        if (mFrameView != null && mAnimDrawable != null) {
            Log.d(TAG, "frame anim start!");
            mFrameView.setVisibility(View.VISIBLE);
            mAnimDrawable.start();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    Log.d(TAG, "frame anim end!");
                    stopFrameAnim();
                    mHandler.removeCallbacks(this);
                }
            }, mAnimDuration);
        }
    }

    public void stopFrameAnim() {
        if (mAnimDrawable != null) {
            mAnimDrawable.stop();
//            mAnimDrawable = null;
        }

        if (mFrameView != null) {
//            mFrameView.setImageDrawable(null);
//            mFrameView.setVisibility(View.GONE);
//            mFrameView = null;
//            mFLContainer.removeAllViews();
        }
    }
    
}
