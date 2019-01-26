package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.utils.PopupWindowUtils;

public class PopupWindowFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = PopupWindowFragment.class.getSimpleName();

    private PopupWindow mPopupWindow;
    private PopupWindowUtils.AnchorGravity mGravity;
    private int mHorizontalGravity;
    private int mVerticalGravity;

    private RadioGroup hrg;
    private RadioGroup vrg;

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

        View view = inflater.inflate(R.layout.fragment_popup_window, container, false);

        initPopupWindow();

        hrg = view.findViewById(R.id.rg_horizontal);
        hrg.setOnCheckedChangeListener(this);
        vrg = view.findViewById(R.id.rg_vertical);
        vrg.setOnCheckedChangeListener(this);

        Button button = view.findViewById(R.id.btn_popup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        return view;
    }

    private void initPopupWindow() {
        View contentView = new View(getActivity());
        contentView.setBackgroundColor(Color.parseColor("#9effff00"));

        mPopupWindow = new PopupWindow(contentView, 300, 300, true);
        mPopupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法关闭弹窗
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mGravity = new PopupWindowUtils.AnchorGravity(0);
    }

    private void showPopupWindow(View anchor) {
        // mPopupWindow.showAsDropDown(view, -300, 0);
        // PopupWindowUtils不适合使用 LayoutParams.WRAP_CONTENT 和 LayoutParams.MATCH_PARENT 的 PopupWindow
        mGravity.setGravity(mHorizontalGravity | mVerticalGravity);
        PopupWindowUtils.show(anchor, mPopupWindow, mGravity, 0, 0);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mGravity == null) {
            return;
        }
        if (group == hrg) {
            switch (checkedId) {
                case R.id.rb_al:
                    mHorizontalGravity = PopupWindowUtils.AnchorGravity.ALIGN_LEFT;
                    break;
                case R.id.rb_ar:
                    mHorizontalGravity = PopupWindowUtils.AnchorGravity.ALIGN_RIGHT;
                    break;
                case R.id.rb_tl:
                    mHorizontalGravity = PopupWindowUtils.AnchorGravity.TO_LEFT;
                    break;
                case R.id.rb_tr:
                    mHorizontalGravity = PopupWindowUtils.AnchorGravity.TO_RIGHT;
                    break;
                case R.id.rb_hc:
                    mHorizontalGravity = PopupWindowUtils.AnchorGravity.HORIZONTAL_CENTER;
                    break;
            }
        } else {
            switch (checkedId) {
                case R.id.rb_at:
                    mVerticalGravity = PopupWindowUtils.AnchorGravity.ALIGN_TOP;
                    break;
                case R.id.rb_ab:
                    mVerticalGravity = PopupWindowUtils.AnchorGravity.ALIGN_BOTTOM;
                    break;
                case R.id.rb_tt:
                    mVerticalGravity = PopupWindowUtils.AnchorGravity.TO_TOP;
                    break;
                case R.id.rb_tb:
                    mVerticalGravity = PopupWindowUtils.AnchorGravity.TO_BOTTOM;
                    break;
                case R.id.rb_vc:
                    mVerticalGravity = PopupWindowUtils.AnchorGravity.VERTICAL_CENTER;
                    break;
            }
        }

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
