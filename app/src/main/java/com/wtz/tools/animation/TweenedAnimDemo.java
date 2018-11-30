package com.wtz.tools.animation;

import android.util.Log;
import android.view.View;
import android.view.animation.*;

/**
 * 补间动画示例
 */
public class TweenedAnimDemo {

    public static final String TAG = TweenedAnimDemo.class.getName();

    private static final int RELA1 = Animation.RELATIVE_TO_SELF;
    private static final int RELA2 = Animation.RELATIVE_TO_PARENT;

    public static Interpolator INTERPOLATOR = new LinearInterpolator();

    public static void baseIn(View view, Animation animation, long durationMillis, long delayMillis) {
        animation.setInterpolator(INTERPOLATOR);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "baseIn...onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "baseIn...onAnimationRepeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "baseIn...onAnimationEnd");
            }
        });
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    public static void baseOut(final View view, Animation animation, long durationMillis, long delayMillis) {
        animation.setInterpolator(INTERPOLATOR);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "baseOut...onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "baseOut...onAnimationRepeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "baseOut...onAnimationEnd");
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(animation);
    }

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void transparent(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void fadeIn(View view, long durationMillis, long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void fadeOut(View view, long durationMillis, long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void slideIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(RELA2, 1, RELA2, 0, RELA2, 0, RELA2, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void slideOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(RELA2, 0, RELA2, -1, RELA2, 0, RELA2, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void scaleIn(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, RELA2, 0.5f, RELA2, 0.5f);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void scaleOut(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, RELA2, 0.5f, RELA2, 0.5f);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void rotateIn(View view, long durationMillis, long delayMillis) {
        RotateAnimation animation = new RotateAnimation(-90, 0, RELA1, 0, RELA1, 1);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void rotateOut(View view, long durationMillis, long delayMillis) {
        RotateAnimation animation = new RotateAnimation(0, 90, RELA1, 0, RELA1, 1);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void scaleRotateIn(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(0, 1, 0, 1, RELA1, 0.5f, RELA1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0, 360, RELA1, 0.5f, RELA1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void scaleRotateOut(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(1, 0, 1, 0, RELA1, 0.5f, RELA1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0, 360, RELA1, 0.5f, RELA1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void slideFadeIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(RELA2, 1, RELA2, 0, RELA2, 0, RELA2, 0);
        AlphaAnimation animation2 = new AlphaAnimation(0, 1);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    public static void slideFadeOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(RELA2, 0, RELA2, -1, RELA2, 0, RELA2, 0);
        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    public static void swingUpDown(View view) {
        int rela1 = Animation.RELATIVE_TO_SELF;
        int rela2 = Animation.RELATIVE_TO_PARENT;
        TranslateAnimation anim = new TranslateAnimation(rela1, 0, rela1, 0, rela1, 0, rela1, 0.2f);
        anim.setDuration(350);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }

}
