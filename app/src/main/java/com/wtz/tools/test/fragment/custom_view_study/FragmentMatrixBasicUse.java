package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.MatrixBasicUse;

public class FragmentMatrixBasicUse extends Fragment {

    public FragmentMatrixBasicUse() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_matrix_basic_use, container, false);
        final MatrixBasicUse matrixBasicUse = (MatrixBasicUse) root.findViewById(R.id.matrix_basic_use);

        RadioGroup group = (RadioGroup) root.findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.point0:
                        matrixBasicUse.setTestMode(0);
                        break;
                    case R.id.point1:
                        matrixBasicUse.setTestMode(1);
                        break;
                    case R.id.point2:
                        matrixBasicUse.setTestMode(2);
                        break;
                    case R.id.point3:
                        matrixBasicUse.setTestMode(3);
                        break;
                    case R.id.point4:
                        matrixBasicUse.setTestMode(4);
                        break;
                }
            }
        });
        return root;
    }

}
