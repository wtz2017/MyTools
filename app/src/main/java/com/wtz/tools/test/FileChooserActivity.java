package com.wtz.tools.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.wtz.tools.R;
import com.wtz.tools.utils.file.FileChooser;


public class FileChooserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FileChooserActivity";

    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;

    private int mRequestCode1;
    private int mRequestCode2;
    private int mRequestCode3;
    private int mRequestCode4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        mText1 = findViewById(R.id.tv1);
        mText2 = findViewById(R.id.tv2);
        mText3 = findViewById(R.id.tv3);
        mText4 = findViewById(R.id.tv4);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                mRequestCode1 = FileChooser.chooseFile(this);
                Log.d(TAG, "onClick btn_1 requestCode=" + mRequestCode1);
                break;
            case R.id.btn_2:
                mRequestCode2 = FileChooser.chooseImage(this);
                Log.d(TAG, "onClick btn_2 requestCode=" + mRequestCode2);
                break;
            case R.id.btn_3:
                mRequestCode3 = FileChooser.chooseAudio(this);
                Log.d(TAG, "onClick btn_3 requestCode=" + mRequestCode3);
                break;
            case R.id.btn_4:
                mRequestCode4 = FileChooser.chooseVideo(this);
                Log.d(TAG, "onClick btn_4 requestCode=" + mRequestCode4);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseResult(FileChooser.ChooseResult chooseResult) {
        Log.d(TAG, "onResult requestCode=" + chooseResult.getRequestCode()
                + "; filePath=" + chooseResult.getFilePath());
        if (chooseResult.getRequestCode() == mRequestCode1) {
            mText1.setText("" + chooseResult.getFilePath());
        } else if (chooseResult.getRequestCode() == mRequestCode2) {
            mText2.setText("" + chooseResult.getFilePath());
        } else if (chooseResult.getRequestCode() == mRequestCode3) {
            mText3.setText("" + chooseResult.getFilePath());
        } else if (chooseResult.getRequestCode() == mRequestCode4) {
            mText4.setText("" + chooseResult.getFilePath());
        }
    }

}
