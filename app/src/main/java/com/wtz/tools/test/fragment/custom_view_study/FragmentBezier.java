package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.bezier.Bezier3Order;

public class FragmentBezier extends Fragment {

    public FragmentBezier() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bezier, container, false);

        final Bezier3Order bezier3 = (Bezier3Order) root.findViewById(R.id.bezier3);

        RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.rg_ctrl_mode);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ctrl2:
                        bezier3.setControlMode(false);
                        break;

                    case R.id.ctrl1:
                    default:
                        bezier3.setControlMode(true);
                        break;
                }
            }
        });
        return root;
    }

}
