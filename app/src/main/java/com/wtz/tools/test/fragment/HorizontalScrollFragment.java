package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.view.HorizontalScrollSlideView;

import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollFragment extends Fragment {
    private static final String TAG = HorizontalScrollFragment.class.getSimpleName();

    private List<View> mViews = new ArrayList<>();

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 10; i++) {
            mViews.add(getViewItem(i));
        }
    }

    private View getViewItem(int i) {
        TextView textView = new TextView(getActivity());
        textView.setText("Item-" + i);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.horizontal_item_bg);
        textView.setPadding(15, 50, 15, 50);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = 15;
        textView.setLayoutParams(lp);
        return textView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_horizontal_scroll_view, container, false);
        HorizontalScrollSlideView scrollSlideView = view.findViewById(R.id.horizontal_scroll_slide_view);
        scrollSlideView.setContentViews(mViews);
        scrollSlideView.setOnSlideBottomListener(new HorizontalScrollSlideView.OnSlideBottomListener() {
            @Override
            public void onSlideBottom() {
                Log.d(TAG, "HorizontalScrollSlideView...onSlideBottom");
            }
        });

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
