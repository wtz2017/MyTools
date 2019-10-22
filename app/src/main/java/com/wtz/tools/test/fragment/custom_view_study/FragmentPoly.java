package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.SetPolyToPoly;

public class FragmentPoly extends Fragment {

    public FragmentPoly() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_poly, container, false);

        final SetPolyToPoly poly = (SetPolyToPoly) root.findViewById(R.id.poly);

        RadioGroup group = (RadioGroup) root.findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.point0:
                        poly.setTestPoint(0);
                        break;
                    case R.id.point1:
                        poly.setTestPoint(1);
                        break;
                    case R.id.point2:
                        poly.setTestPoint(2);
                        break;
                    case R.id.point3:
                        poly.setTestPoint(3);
                        break;
                    case R.id.point4:
                        poly.setTestPoint(4);
                        break;
                }
            }
        });
        return root;
    }

}
