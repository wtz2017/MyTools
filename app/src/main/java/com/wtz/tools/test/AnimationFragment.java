package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.wtz.tools.R;
import com.wtz.tools.animation.FrameAnimDemo;
import com.wtz.tools.animation.Roate3Lines;
import com.wtz.tools.animation.SinAnimView;
import com.wtz.tools.animation.PropertyAnimDemo;
import com.wtz.tools.animation.TweenedAnimDemo;
import com.wtz.tools.test.adapter.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnimationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = AnimationFragment.class.getSimpleName();

    private ImageView frameView;
    private FrameAnimDemo frameAnimDemo;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Button startFrameButton;

    private View tweenedTarget;
    private Spinner mSpinner;
    private Button fadeInButton, fadeOutButton, slideInButton, slideOutButton, scaleInButton, scaleOutButton, rotateInButton, rotateOutButton, scaleRotateInButton, scaleRotateOutButton,
            slideFadeInButton, slideFadeOutButton, upDownButton, rotateYButton, stopRotateYButton, swingUpDown;

    private View propertyTarget;
    private Button objAnimRotationXButton;
    private Button objAnimRotationYButton;
    private Button objAnimRotationZButton;
    private Button objAnimAlphaButton;
    private Button objAnimScaleXButton;
    private Button objAnimScaleYButton;
    private Button objTranslationXButton;
    private Button objTranslationYButton;

    private SinAnimView mSinAnimView;
    private Button objStartSinButton;
    private Button objStopSinButton;

    private View line1;
    private View line2;
    private View line3;
    private Roate3Lines mRoate3Lines;
    private Button startRoate3linesButton;
    private Button stopRoate3linesButton;

    private List<InterpolatorItem> mInterpolatorItems = new ArrayList<>();
    private long durationMillis = 2000, delayMillis = 0;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_animation, container, false);

        frameView = view.findViewById(R.id.iv_frame_anim);
        frameAnimDemo = new FrameAnimDemo();
        frameAnimDemo.initFrameAnim(frameView, handler);
        startFrameButton = view.findViewById(R.id.start_frame_anim_button);

        tweenedTarget = view.findViewById(R.id.v_target);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_interpolator);
        mSpinner.setAdapter(getAdapter());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TweenedAnimDemo.INTERPOLATOR = mInterpolatorItems.get(position).interpolator;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setSelection(0);

        fadeInButton = (Button) view.findViewById(R.id.fadeInButton);
        fadeOutButton = (Button) view.findViewById(R.id.fadeOutButton);
        slideInButton = (Button) view.findViewById(R.id.slideInButton);
        slideOutButton = (Button) view.findViewById(R.id.slideOutButton);
        scaleInButton = (Button) view.findViewById(R.id.scaleInButton);
        scaleOutButton = (Button) view.findViewById(R.id.scaleOutButton);
        rotateInButton = (Button) view.findViewById(R.id.rotateInButton);
        rotateOutButton = (Button) view.findViewById(R.id.rotateOutButton);
        scaleRotateInButton = (Button) view.findViewById(R.id.scaleRotateInButton);
        scaleRotateOutButton = (Button) view.findViewById(R.id.scaleRotateOutButton);
        slideFadeInButton = (Button) view.findViewById(R.id.slideFadeInButton);
        slideFadeOutButton = (Button) view.findViewById(R.id.slideFadeOutButton);
        upDownButton = (Button) view.findViewById(R.id.upDownButton);

        propertyTarget = view.findViewById(R.id.v_target_property);
        objAnimRotationXButton = (Button) view.findViewById(R.id.oa_rotation_x_button);
        objAnimRotationYButton = (Button) view.findViewById(R.id.oa_rotation_y_button);
        objAnimRotationZButton = (Button) view.findViewById(R.id.oa_rotation_z_button);
        objAnimAlphaButton = (Button) view.findViewById(R.id.oa_alpha_button);
        objAnimScaleXButton = (Button) view.findViewById(R.id.oa_scale_x_button);
        objAnimScaleYButton = (Button) view.findViewById(R.id.oa_scale_y_button);
        objTranslationXButton = (Button) view.findViewById(R.id.oa_translation_x_button);
        objTranslationYButton = (Button) view.findViewById(R.id.oa_translation_y_button);

        mSinAnimView = view.findViewById(R.id.oa_sin_animView);
        objStartSinButton = view.findViewById(R.id.start_sin_button);
        objStopSinButton = view.findViewById(R.id.stop_sin_button);

        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);
        mRoate3Lines = new Roate3Lines(line1, line2, line3);
        startRoate3linesButton = view.findViewById(R.id.start_roate3lines_button);
        stopRoate3linesButton = view.findViewById(R.id.stop_roate3lines_button);

        startFrameButton.setOnClickListener(this);
        fadeInButton.setOnClickListener(this);
        fadeOutButton.setOnClickListener(this);
        slideInButton.setOnClickListener(this);
        slideOutButton.setOnClickListener(this);
        scaleInButton.setOnClickListener(this);
        scaleOutButton.setOnClickListener(this);
        rotateInButton.setOnClickListener(this);
        rotateOutButton.setOnClickListener(this);
        scaleRotateInButton.setOnClickListener(this);
        scaleRotateOutButton.setOnClickListener(this);
        slideFadeInButton.setOnClickListener(this);
        slideFadeOutButton.setOnClickListener(this);
        upDownButton.setOnClickListener(this);

        objAnimRotationXButton.setOnClickListener(this);
        objAnimRotationYButton.setOnClickListener(this);
        objAnimRotationZButton.setOnClickListener(this);
        objAnimAlphaButton.setOnClickListener(this);
        objAnimScaleXButton.setOnClickListener(this);
        objAnimScaleYButton.setOnClickListener(this);
        objTranslationXButton.setOnClickListener(this);
        objTranslationYButton.setOnClickListener(this);
        objStartSinButton.setOnClickListener(this);
        objStopSinButton.setOnClickListener(this);
        startRoate3linesButton.setOnClickListener(this);
        stopRoate3linesButton.setOnClickListener(this);

        return view;
    }

    @NonNull
    private BaseAdapter getAdapter() {
        mInterpolatorItems.clear();
        mInterpolatorItems.add(new InterpolatorItem("LinearInterpolator", new LinearInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("AccelerateInterpolator", new AccelerateInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("DecelerateInterpolator", new DecelerateInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("AccelerateDecelerateInterpolator", new AccelerateDecelerateInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("BounceInterpolator", new BounceInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("OvershootInterpolator", new OvershootInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("AnticipateInterpolator", new AnticipateInterpolator()));
        mInterpolatorItems.add(new InterpolatorItem("AnticipateOvershootInterpolator", new AnticipateOvershootInterpolator()));
        return new SpinnerAdapter(getActivity(), mInterpolatorItems);
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

    @Override
    public void onClick(View v) {
        if (v == startFrameButton) {
            frameAnimDemo.startFrameAnim();
        } if (v == fadeInButton) {
            TweenedAnimDemo.fadeIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == fadeOutButton) {
            TweenedAnimDemo.fadeOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == slideInButton) {
            TweenedAnimDemo.slideIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == slideOutButton) {
            TweenedAnimDemo.slideOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == scaleInButton) {
            TweenedAnimDemo.scaleIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == scaleOutButton) {
            TweenedAnimDemo.scaleOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == rotateInButton) {
            TweenedAnimDemo.rotateIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == rotateOutButton) {
            TweenedAnimDemo.rotateOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == scaleRotateInButton) {
            TweenedAnimDemo.scaleRotateIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == scaleRotateOutButton) {
            TweenedAnimDemo.scaleRotateOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == slideFadeInButton) {
            TweenedAnimDemo.slideFadeIn(tweenedTarget, durationMillis, delayMillis);
        } else if (v == slideFadeOutButton) {
            TweenedAnimDemo.slideFadeOut(tweenedTarget, durationMillis, delayMillis);
        } else if (v == upDownButton) {
            TweenedAnimDemo.swingUpDown(tweenedTarget);
        } else if (v == objAnimRotationZButton) {
            PropertyAnimDemo.rotateZ(propertyTarget);
        } else if (v == objAnimRotationXButton) {
            PropertyAnimDemo.rotateX(propertyTarget);
        } else if (v == objAnimRotationYButton) {
            PropertyAnimDemo.rotateY(propertyTarget);
        } else if (v == objAnimAlphaButton) {
            PropertyAnimDemo.alpah(propertyTarget);
        } else if (v == objAnimScaleXButton) {
            PropertyAnimDemo.scaleX(propertyTarget);
        } else if (v == objAnimScaleYButton) {
            PropertyAnimDemo.scaleY(propertyTarget);
        } else if (v == objTranslationXButton) {
            PropertyAnimDemo.translationX(propertyTarget);
        } else if (v == objTranslationYButton) {
            PropertyAnimDemo.translationY(propertyTarget);
        } else if (v == objStartSinButton) {
            mSinAnimView.startAnimation();
        } else if (v == objStopSinButton) {
            mSinAnimView.stopAnimation();
        } else if (v == startRoate3linesButton) {
            mRoate3Lines.startRotate();
        } else if (v == stopRoate3linesButton) {
            mRoate3Lines.stopRotate();
        }
    }

    public class InterpolatorItem {

        public InterpolatorItem(String name, Interpolator interpolator) {
            this.name = name;
            this.interpolator = interpolator;
        }

        public String name;
        public Interpolator interpolator;

    }

}
