package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.TouchRegion;

public class FragmentTouchRegion extends Fragment {

    public FragmentTouchRegion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_touch_region, container, false);

        final TouchRegion touchRegion = (TouchRegion) root.findViewById(R.id.touch_region);
        RadioGroup group = (RadioGroup) root.findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.point0:
                        touchRegion.setTestMode(0);
                        break;
                    case R.id.point1:
                        touchRegion.setTestMode(1);
                        break;
                }
            }
        });

        return root;
    }

}
