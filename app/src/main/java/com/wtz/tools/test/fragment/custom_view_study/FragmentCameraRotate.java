package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.CameraRotate;
import com.wtz.tools.animation.Rotate3dAnimation;

public class FragmentCameraRotate extends Fragment {

    public FragmentCameraRotate() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_camera_rotate, container, false);

        // 你可以封装一个自定义view来实现效果，也可以像后边一样封装一个独立动画来调用实现效果
        final CameraRotate cameraRotate = (CameraRotate) root.findViewById(R.id.camera_rotate);
        RadioGroup group = (RadioGroup) root.findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.point0:
                        cameraRotate.setTestMode(0);
                        break;
                    case R.id.point1:
                        cameraRotate.setTestMode(1);
                        break;
                    case R.id.point2:
                        cameraRotate.setTestMode(2);
                        break;
                }
            }
        });

        // 封装一个独立动画来调用实现效果
        ImageView view = (ImageView) root.findViewById(R.id.iv_image1);
        assert view != null;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 计算中心点（这里是使用view的中心作为旋转的中心点）
                final float centerX = v.getWidth() / 2.0f;
                final float centerY = v.getHeight() / 2.0f;

                //括号内参数分别为（上下文，开始角度，结束角度，x轴中心点，y轴中心点，深度，是否扭曲）
                final Rotate3dAnimation rotation = new Rotate3dAnimation(getActivity(), 0, 180, centerX, centerY, 0f, true);

                rotation.setDuration(3000);                         //设置动画时长
                rotation.setFillAfter(true);                        //保持旋转后效果
                rotation.setInterpolator(new LinearInterpolator());    //设置插值器
                v.startAnimation(rotation);
            }
        });

        return root;
    }

}
