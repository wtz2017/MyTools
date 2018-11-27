package com.wtz.tools.animation;

import android.animation.ObjectAnimator;
import android.view.View;

public class PropertyAnimDemo {

    public static void rotateZ(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "rotation",
                0f, 360f);
        anim.setDuration(2000);
        anim.start();
    }

    public static void rotateX(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "rotationX",
                0f, 360f);
        anim.setDuration(2000);
        anim.start();
    }

    public static void rotateY(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "rotationY",
                0f, 360f);
        anim.setDuration(2000);
        anim.start();
    }

    public static void alpah(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "alpha",
                1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.0f,
                0.2f, 0.4f, 0.6f, 0.8f, 1.0f);
//        anim.setRepeatCount(-1);
//        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setDuration(3000);
        anim.start();
    }

    public static void scaleX(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "scaleX",
                0.0f, 1.0f);
        anim.setDuration(2000);
        anim.start();
    }

    public static void scaleY(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "scaleY",
                0.0f, 2.0f);
        anim.setDuration(2000);
        anim.start();
    }

    public static void translationX(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "translationX",
                100, 400, 0, -100, 0);
        anim.setDuration(2000);
        anim.start();
    }

    public static void translationY(View target) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                target, "translationY",
                100, 200, 100, 0, -100, -200, -100, 0);
        anim.setDuration(2000);
        anim.start();
    }

}
