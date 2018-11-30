package com.wtz.tools.animation;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.view.View;

public class Roate3Lines {
	View line1;
	View line2;
	View line3;

	AnimatorSet mAnimSet;
	
	public static int totalDuration = 600;
	public static int totalInterval = 120;
	
	public static int line1Delay = 0;
	public static int line2Delay = 80;
	public static int line3Delay = 160;

	private Roate3Lines() { }

	public Roate3Lines(View line1, View line2, View line3) {
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
	}

	public void startRotate() {
		if (null == mAnimSet) {
			mAnimSet = new AnimatorSet();
		}

		Keyframe y0 = Keyframe.ofFloat(0f, 0f);
		Keyframe y1 = Keyframe.ofFloat(0.1f, -18f);
		Keyframe y2 = Keyframe.ofFloat(0.2f, -36f);
		Keyframe y3 = Keyframe.ofFloat(0.3f, -54f);
		Keyframe y4 = Keyframe.ofFloat(0.4f, -72f);
		Keyframe y5 = Keyframe.ofFloat(0.5f, -90f);
		Keyframe y6 = Keyframe.ofFloat(0.6f, -108f);
		Keyframe y7 = Keyframe.ofFloat(0.7f, -126f);
		Keyframe y8 = Keyframe.ofFloat(0.8f, -144f);
		Keyframe y9 = Keyframe.ofFloat(0.9f, -162f);
		Keyframe y10 = Keyframe.ofFloat(1f, -180f);
		Keyframe y11 = Keyframe.ofFloat(0.9f, -162f);
		Keyframe y12 = Keyframe.ofFloat(0.8f, -144f);
		Keyframe y13 = Keyframe.ofFloat(0.7f, -126f);
		Keyframe y14 = Keyframe.ofFloat(0.6f, -108f);
		Keyframe y15 = Keyframe.ofFloat(0.5f, -90f);
		Keyframe y16 = Keyframe.ofFloat(0.4f, -72f);
		Keyframe y17 = Keyframe.ofFloat(0.3f, -54f);
		Keyframe y18 = Keyframe.ofFloat(0.2f, -36f);
		Keyframe y19 = Keyframe.ofFloat(0.1f, -18f);
		Keyframe y20 = Keyframe.ofFloat(0f, 0f);
		PropertyValuesHolder pvhRotationY = PropertyValuesHolder.ofKeyframe("rotationY", y0, y1, y2, y3, y4, y5, y6, y7, y8, y9, y10
				, y11, y12, y13, y14, y15, y16, y17, y18, y19, y20);

		Keyframe z0 = Keyframe.ofFloat(0f, 0f);
		Keyframe z1 = Keyframe.ofFloat(0.1f, 0f);
		Keyframe z2 = Keyframe.ofFloat(0.2f, 0f);
		Keyframe z3 = Keyframe.ofFloat(0.3f, 0f);
		Keyframe z4 = Keyframe.ofFloat(0.4f, 0f);
		Keyframe z5 = Keyframe.ofFloat(0.5f, 0f);
		Keyframe z6 = Keyframe.ofFloat(0.6f, 0f);
		Keyframe z7 = Keyframe.ofFloat(0.7f, 0f);
		Keyframe z8 = Keyframe.ofFloat(0.8f, 0f);
		Keyframe z9 = Keyframe.ofFloat(0.9f, 0f);
		Keyframe z10 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder pvhRotationZ = PropertyValuesHolder.ofKeyframe("rotation", z0, z1, z2, z3, z4, z5, z6, z7, z8, z9, z10);

		ObjectAnimator rotationAnim1 = ObjectAnimator.ofPropertyValuesHolder(line3, pvhRotationY,
				pvhRotationZ);
		rotationAnim1.setStartDelay(line1Delay);

		ObjectAnimator rotationAnim2 = ObjectAnimator.ofPropertyValuesHolder(line2, pvhRotationY,
				pvhRotationZ);
		rotationAnim2.setStartDelay(line2Delay);

		ObjectAnimator rotationAnim3 = ObjectAnimator.ofPropertyValuesHolder(line1, pvhRotationY,
				pvhRotationZ);
		rotationAnim3.setStartDelay(line3Delay);

		rotationAnim3.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (null != mAnimSet) {
					mAnimSet.start();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		mAnimSet.setDuration(totalDuration);
		mAnimSet.setStartDelay(totalInterval);
		mAnimSet.playTogether(rotationAnim1, rotationAnim2, rotationAnim3);
		mAnimSet.start();
	}
	
	public void stopRotate() {
		if (null != mAnimSet) {
			mAnimSet.cancel();
			mAnimSet = null;
		}
	}
}
