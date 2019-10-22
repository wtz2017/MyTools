package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.PieData;
import com.wtz.tools.view.custom_view_study.PieGraph;

import java.util.ArrayList;

public class FragmentPie extends Fragment {

    private PieGraph mPieGraph;

    public FragmentPie() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pie, container, false);
        mPieGraph = (PieGraph) root.findViewById(R.id.pie);
        mPieGraph.setStartAngle(0);
        ArrayList<PieData> datas = new ArrayList<>();
        datas.add(new PieData("a", 10));
        datas.add(new PieData("b", 60));
        datas.add(new PieData("c", 20));
        datas.add(new PieData("d", 80));
        datas.add(new PieData("e", 30));
        datas.add(new PieData("f", 50));
        mPieGraph.setData(datas);
        return root;
    }

}
