package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
import android.widget.Spinner;

import com.wtz.tools.R;
import com.wtz.tools.animation.TweenedAnimation;
import com.wtz.tools.test.adapter.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnimationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = AnimationFragment.class.getSimpleName();

    private View target;
    private Spinner mSpinner;
    private Button fadeInButton, fadeOutButton, slideInButton, slideOutButton, scaleInButton, scaleOutButton, rotateInButton, rotateOutButton, scaleRotateInButton, scaleRotateOutButton,
            slideFadeInButton, slideFadeOutButton, rotateYButton, stopRotateYButton, swingUpDown;

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

        target = view.findViewById(R.id.v_target);

        mSpinner = (Spinner) view.findViewById(R.id.spinner_interpolator);
        mSpinner.setAdapter(getAdapter());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TweenedAnimation.INTERPOLATOR = mInterpolatorItems.get(position).interpolator;
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
        rotateYButton = (Button) view.findViewById(R.id.rotateYButton);
        stopRotateYButton = (Button) view.findViewById(R.id.stop_rotateY_button);
        swingUpDown = (Button) view.findViewById(R.id.btn_swing_up_down);

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
        if (v == fadeInButton) {
            TweenedAnimation.fadeIn(target, durationMillis, delayMillis);
        } else if (v == fadeOutButton) {
            TweenedAnimation.fadeOut(target, durationMillis, delayMillis);
        } else if (v == slideInButton) {
            TweenedAnimation.slideIn(target, durationMillis, delayMillis);
        } else if (v == slideOutButton) {
            TweenedAnimation.slideOut(target, durationMillis, delayMillis);
        } else if (v == scaleInButton) {
            TweenedAnimation.scaleIn(target, durationMillis, delayMillis);

        } else if (v == scaleOutButton) {
            TweenedAnimation.scaleOut(target, durationMillis, delayMillis);
        } else if (v == rotateInButton) {
            TweenedAnimation.rotateIn(target, durationMillis, delayMillis);
        } else if (v == rotateOutButton) {
            TweenedAnimation.rotateOut(target, durationMillis, delayMillis);
        } else if (v == scaleRotateInButton) {
            TweenedAnimation.scaleRotateIn(target, durationMillis, delayMillis);

        } else if (v == scaleRotateOutButton) {
            TweenedAnimation.scaleRotateOut(target, durationMillis, delayMillis);
        } else if (v == slideFadeInButton) {
            TweenedAnimation.slideFadeIn(target, durationMillis, delayMillis);
        } else if (v == slideFadeOutButton) {
            TweenedAnimation.slideFadeOut(target, durationMillis, delayMillis);
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
