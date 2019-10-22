package com.wtz.tools.test.fragment.custom_view_study;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wtz.tools.R;

public class FragmentMultitouch extends Fragment {

    public FragmentMultitouch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_multitouch, container, false);
        return root;
    }

}
