package com.wtz.tools.animation;

import android.util.Log;
import android.view.View;
import android.view.animation.*;

/**
 * 补间动画示例
 */
public class TweenedAnimation {

    public static final String TAG = TweenedAnimation.class.getName();

    private static final int RELA1 = Animation.RELATIVE_TO_SELF;
    private static final int RELA2 = Animation.RELATIVE_TO_PARENT;

    public static Interpolator INTERPOLATOR = new LinearInterpolator();

//    public static void setEffect(Animation animation, int interpolatorType, long durationMillis, long delayMillis) {
//        switch (interpolatorType) {
//            case 0:
//                animation.setInterpolator(new LinearInterpolator());
//                break;
//            case 1:
//                animation.setInterpolator(new AccelerateInterpolator());
//                break;
//            case 2:
//                animation.setInterpolator(new DecelerateInterpolator());
//                break;
//            case 3:
//                animation.setInterpolator(new AccelerateDecelerateInterpolator());
//                break;
//            case 4:
//                animation.setInterpolator(new BounceInterpolator());
//                break;
//            case 5:
//                animation.setInterpolator(new OvershootInterpolator());
//                break;
//            case 6:
//                animation.setInterpolator(new AnticipateInterpolator());
//                break;
//            case 7:
//                animation.setInterpolator(new AnticipateOvershootInterpolator());
//                break;
//            default:
//                break;
//        }
//        animation.setDuration(durationMillis);
//        animation.setStartOffset(delayMillis);
//    }

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

//    ObjectAnimator rotationAnim;
//    ObjectAnimator rotationAnim1;
//    ObjectAnimator rotationAnim2;
//    ObjectAnimator rotationAnim3;
//    public static void rotateY(final View view, long durationMillis, long delayMillis)
//    {
//        // 方法1
//////		Rotate3dAnimation r = new Rotate3dAnimation(0, 180, view.getWidth() / 2, view.getHeight() / 2, 310.0f, true);
////		Rotate3dAnimation r = new Rotate3dAnimation(0, 180, view.getWidth() / 2, view.getHeight() / 2, 0.0f, true);
////		r.setDuration(durationMillis);
////		r.setFillAfter(true);
////		view.startAnimation(r);
//
////		// 方法2
////		view.setPivotX(view.getWidth() / 2);
////		view.setPivotY(view.getHeight() / 2);
////		view.animate().rotationY(180).setDuration(2000);
//
////		// 方法3
////		ViewPropertyAnimator animator = view.animate();//该对象没有setRepeat的方法
////
////		view.setPivotX(view.getWidth() / 2);
////		view.setPivotY(view.getHeight() / 2);
////        animator.rotationY(180);
////        animator.setDuration(2000);
////        animator.setStartDelay(delayMillis);
////        animator.setListener(new AnimatorListener() {
////
////			@Override
////			public static void onAnimationStart(Animator animation) {
////				// TODO Auto-generated method stub
////
////			}
////
////			@Override
////			public static void onAnimationRepeat(Animator animation) {
////				// TODO Auto-generated method stub
////
////			}
////
////			@Override
////			public static void onAnimationEnd(Animator animation) {
////				// TODO Auto-generated method stub
////				view.clearAnimation();
////			}
////
////			@Override
////			public static void onAnimationCancel(Animator animation) {
////				// TODO Auto-generated method stub
////
////			}
////		});
////        animator.start();
//
//        // 方法4
//        Keyframe y0 = Keyframe.ofFloat(0f, 0f);
//        Keyframe y1 = Keyframe.ofFloat(1f, 180f);
//        PropertyValuesHolder pvhRotationY = PropertyValuesHolder.ofKeyframe("rotationY", y0, y1);
//
//        Keyframe z0 = Keyframe.ofFloat(0f, 0f);
//        Keyframe z1 = Keyframe.ofFloat(0.5f, -30f);
//        Keyframe z2 = Keyframe.ofFloat(0.5f, -30f);
//        Keyframe z3 = Keyframe.ofFloat(1f, 0f);
//        PropertyValuesHolder pvhRotationZ = PropertyValuesHolder.ofKeyframe("rotation", z0, z1, z2, z3);
//
//        rotationAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhRotationY, pvhRotationZ);
//        rotationAnim.setDuration(durationMillis);
////		rotationAnim.setInterpolator(new AccelerateInterpolator(8));
//        rotationAnim.setStartDelay(delayMillis);
//        rotationAnim.setRepeatMode(Animation.RESTART);
//        rotationAnim.setRepeatCount(Animation.INFINITE);
//        rotationAnim.start();
//    }
//
//    public static void rotateY(final View view1, final View view2, final View view3, long durationMillis, long delayMillis)
//    {
//        Keyframe y0 = Keyframe.ofFloat(0f, 0f);
//        Keyframe y1 = Keyframe.ofFloat(0.6f, 180f);
//        Keyframe y2 = Keyframe.ofFloat(1f, 180f);
//        PropertyValuesHolder pvhRotationY = PropertyValuesHolder.ofKeyframe("rotationY", y0, y1);
//
//        Keyframe z0 = Keyframe.ofFloat(0f, 0f);
//        Keyframe z1 = Keyframe.ofFloat(0.3f, -15f);
//        Keyframe z2 = Keyframe.ofFloat(0.3f, -15f);
//        Keyframe z3 = Keyframe.ofFloat(0.6f, 0f);
//        Keyframe z4 = Keyframe.ofFloat(1f, 0f);
//        PropertyValuesHolder pvhRotationZ = PropertyValuesHolder.ofKeyframe("rotation", z0, z1, z2, z3);
//
//        rotationAnim1 = ObjectAnimator.ofPropertyValuesHolder(view1, pvhRotationY, pvhRotationZ);
//        rotationAnim2 = ObjectAnimator.ofPropertyValuesHolder(view2, pvhRotationY, pvhRotationZ);
//        rotationAnim3 = ObjectAnimator.ofPropertyValuesHolder(view3, pvhRotationY, pvhRotationZ);
//
//        rotationAnim1.setDuration(durationMillis);
//        rotationAnim1.setStartDelay(0);
//        rotationAnim1.setRepeatMode(Animation.RESTART);
//        rotationAnim1.setRepeatCount(Animation.INFINITE);
////		rotationAnim1.start();
//
//        rotationAnim2.setDuration(durationMillis);
//        rotationAnim2.setStartDelay(150);
//        rotationAnim2.setRepeatMode(Animation.RESTART);
//        rotationAnim2.setRepeatCount(Animation.INFINITE);
////		rotationAnim2.start();
//
//        rotationAnim3.setDuration(durationMillis);
//        rotationAnim3.setStartDelay(300);
//        rotationAnim3.setRepeatMode(Animation.RESTART);
//        rotationAnim3.setRepeatCount(Animation.INFINITE);
////		rotationAnim3.start();
//
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.setDuration(1300);
//        animSet.playTogether(rotationAnim1, rotationAnim2, rotationAnim3);
//        animSet.start();
//    }


//    private void swingUpDown(View view) {
//        int rela1 = Animation.RELATIVE_TO_SELF;
//        int rela2 = Animation.RELATIVE_TO_PARENT;
////	    TranslateAnimation anim = new TranslateAnimation(rela2, 1, rela2, 0, rela2, 0, rela2, 0);
////	    TranslateAnimation anim = new TranslateAnimation(rela2, 0, rela2, 0, rela2, 1, rela2, 0);
////	    TranslateAnimation anim = new TranslateAnimation(rela1, 0, rela1, 0, rela1, 1, rela1, 0);
//        TranslateAnimation anim = new TranslateAnimation(rela1, 0, rela1, 0, rela1, 0, rela1, 0.2f);
//        anim.setDuration(350);
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setRepeatMode(Animation.REVERSE);
//        view.setVisibility(View.VISIBLE);
//        view.startAnimation(anim);
//    }
//
//    private void startLoading() {
//        if (null == mRoate3Lines) {
//            mRoate3Lines = new Roate3Lines(line1, line2, line3);
//        }
//        mRoate3Lines.startRotateY();
//    }
//
//    private void stopLoading() {
//        if (null != mRoate3Lines) {
//            mRoate3Lines.stopRotateY();
//        }
//    }
//
//    Roate3Lines mRoate3Lines;
//
//    AnimationDrawable animationDrawable;
//
//    void showFlipByFrame() {
//        img_loading.setBackgroundResource(R.anim.please_wait);
//        if (animationDrawable == null) {
//            animationDrawable = (AnimationDrawable) img_loading.getBackground();
//        }
//        if (animationDrawable != null) {
//            animationDrawable.start();
//        }
//    }

}
