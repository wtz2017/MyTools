package com.wtz.tools.test.fragment.custom_view_study;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.dashboard.DashboardView1;
import com.wtz.tools.view.custom_view_study.dashboard.DashboardView2;
import com.wtz.tools.view.custom_view_study.dashboard.DashboardView3;
import com.wtz.tools.view.custom_view_study.dashboard.DashboardView4;
import com.wtz.tools.view.custom_view_study.dashboard.Thermometer;

import java.util.Random;

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    private LinearLayout mDashboard0Layout;
    private DashboardView1 mDashboardView1;
    private DashboardView2 mDashboardView2;
    private DashboardView3 mDashboardView3;
    private DashboardView4 mDashboardView4;

    private boolean isAnimFinished = true;

    private Thermometer thermometer;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mDashboardView1 = (DashboardView1) root.findViewById(R.id.dashboard_view_1);
        mDashboardView2 = (DashboardView2) root.findViewById(R.id.dashboard_view_2);
        mDashboardView3 = (DashboardView3) root.findViewById(R.id.dashboard_view_3);
        mDashboardView4 = (DashboardView4) root.findViewById(R.id.dashboard_view_4);

        mDashboardView1.setOnClickListener(this);
        mDashboardView2.setOnClickListener(this);
        mDashboardView3.setOnClickListener(this);
        mDashboardView4.setOnClickListener(this);

        mDashboardView2.setCreditValueWithAnim(new Random().nextInt(600) + 350);

        mDashboard0Layout = (LinearLayout) root.findViewById(R.id.dashboard0_layout);
        thermometer = new Thermometer(getActivity(), R.drawable.dashboard, 243);
        mDashboard0Layout.addView(thermometer);
        mDashboard0Layout.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboard0_layout:
                int _speed = new Random().nextInt(100);
                int speed_zhizhen = (int) (24 * _speed / 26);          //调节仪表盘指针指向速度与实际速度相同
                thermometer.handTarget = 243 + speed_zhizhen;
                thermometer.postInvalidate();
                break;
            case R.id.dashboard_view_1:
                mDashboardView1.setRealTimeValue(new Random().nextInt(100));

                break;
            case R.id.dashboard_view_2:
                mDashboardView2.setCreditValueWithAnim(new Random().nextInt(950 - 350) + 350);

                break;
            case R.id.dashboard_view_3:
                mDashboardView3.setCreditValue(new Random().nextInt(950 - 350) + 350);

                break;
            case R.id.dashboard_view_4:
                if (isAnimFinished) {
                    ObjectAnimator animator = ObjectAnimator.ofInt(mDashboardView4, "velocity",
                            mDashboardView4.getVelocity(), new Random().nextInt(180));
                    animator.setDuration(1500).setInterpolator(new LinearInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isAnimFinished = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isAnimFinished = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            isAnimFinished = true;
                        }
                    });
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (Integer) animation.getAnimatedValue();
                            mDashboardView4.setVelocity(value);
                        }
                    });
                    animator.start();
                }

                break;
        }
    }
}
