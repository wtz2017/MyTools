package com.wtz.tools.test.fragment.custom_view_study;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.custom_view_study.CheckView;
import com.wtz.tools.view.custom_view_study.Zombie;

public class FragmentAccumulateDrawbitmap extends Fragment {

    public FragmentAccumulateDrawbitmap() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_accumulate_drawbitmap, container, false);
        initCheckView(root);
        initZombieView(root);
        return root;
    }

    private void initCheckView(View root) {
        final CheckView checkView = (CheckView) root.findViewById(R.id.checkview);
        checkView.setBackgroundColor(Color.GRAY);
        checkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkView.requestFocus();
                return false;
            }
        });
        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkView.isCheck()) {
                    checkView.setBackgroundColor(Color.GRAY);
                    checkView.unCheck();
                } else {
                    checkView.setBackgroundColor(Color.GREEN);
                    checkView.check();
                }
            }
        });
    }

    private void initZombieView(View root) {
        final Zombie zombie = (Zombie) root.findViewById(R.id.zombie);
        zombie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zombie.requestFocus();
                return false;
            }
        });
        zombie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!zombie.isStart()) {
                    zombie.start();
                }
            }
        });
    }
}
