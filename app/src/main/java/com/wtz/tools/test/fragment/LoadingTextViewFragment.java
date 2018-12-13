package com.wtz.tools.test.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.wtz.tools.R;
import com.wtz.tools.view.Titanic;
import com.wtz.tools.view.TitanicTextView;

public class LoadingTextViewFragment extends Fragment {
    private static final String TAG = LoadingTextViewFragment.class.getSimpleName();
    
    private TitanicTextView titanicTextView;
    private Titanic titanic;

    private ShimmerTextView shimmerTextView;
    private Shimmer shimmer;

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

        View view = inflater.inflate(R.layout.fragment_loading_textview, container, false);

        titanicTextView = view.findViewById(R.id.titanic_tv);
        titanic = new Titanic();
        titanic.start(titanicTextView);

        shimmerTextView = view.findViewById(R.id.shimmer_tv);
        shimmer = new Shimmer();
        shimmer.setRepeatCount(-1)
                .setDuration(1000)
                .setStartDelay(300)
                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                .setAnimatorListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        shimmer.start(shimmerTextView);

        return view;
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
        titanic.cancel();
        shimmer.cancel();
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
