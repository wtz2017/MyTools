package com.wtz.tools.utils.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.wtz.tools.utils.UriUtil;

import org.greenrobot.eventbus.EventBus;


public class FileChooser extends AppCompatActivity {
    private static final String TAG = "FileChooser";

    public static final String KEY_CHOOSER_TYPE = "key_chooser_type";
    public static final String KEY_REQUEST_CODE = "key_request_code";

    public static final int TYPE_LOCAL_FILE = 1;
    public static final int TYPE_LOCAL_IMAGE = 2;
    public static final int TYPE_LOCAL_AUDIO = 3;
    public static final int TYPE_LOCAL_VIDEO = 4;

    private static final int INVALID_REQUEST_CODE = -1;

    private int mChooseType;
    private static int sGlobleRequestCode;
    private int mRequestCode;

    public static int chooseFile(Context context) {
        return choose(context, TYPE_LOCAL_FILE);
    }

    public static int chooseImage(Context context) {
        return choose(context, TYPE_LOCAL_IMAGE);
    }

    public static int chooseAudio(Context context) {
        return choose(context, TYPE_LOCAL_AUDIO);
    }

    public static int chooseVideo(Context context) {
        return choose(context, TYPE_LOCAL_VIDEO);
    }

    private static int choose(Context context, int type) {
        Intent intent = new Intent(context, FileChooser.class);
        intent.putExtra(KEY_CHOOSER_TYPE, type);
        int requestCode = ++sGlobleRequestCode;
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return requestCode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mChooseType = getIntent().getIntExtra(KEY_CHOOSER_TYPE, TYPE_LOCAL_FILE);
        mRequestCode = getIntent().getIntExtra(KEY_REQUEST_CODE, INVALID_REQUEST_CODE);
        if (mRequestCode == INVALID_REQUEST_CODE) {
            Log.d(TAG, "INVALID_REQUEST_CODE");
            finish();
            return;
        }

        switch (mChooseType) {
            case TYPE_LOCAL_IMAGE:
                requestChooseImage();
                break;
            case TYPE_LOCAL_AUDIO:
                requestChooseAudio();
                break;
            case TYPE_LOCAL_VIDEO:
                requestChooseVideo();
                break;
            case TYPE_LOCAL_FILE:
            default:
                requestChooseFile();
                break;
        }
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
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void requestChooseFile() {
        requestChoose("*/*", "选择文件");
    }

    private void requestChooseImage() {
        requestChoose("image/*", "选择图片");
    }

    private void requestChooseAudio() {
        requestChoose("audio/*", "选择音频");
    }

    private void requestChooseVideo() {
        requestChoose("video/*", "选择视频");
    }

    private void requestChoose(String type, String title) {
        Intent innerIntent = new Intent();
        innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        innerIntent.setType(type);
        innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent wrapperIntent = Intent.createChooser(innerIntent, title);
        this.startActivityForResult(wrapperIntent, mRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = null;
        if (resultCode == RESULT_OK) {
            filePath = UriUtil.getUriPath(this, data.getData());
            Log.d(TAG, "data.getData()=" + data.getData() + ";\nfilePath=" + filePath);
        }
        finish();
        EventBus.getDefault().post(new ChooseResult(filePath, mRequestCode));
    }

    public static class ChooseResult {
        private String filePath;
        private int requestCode;

        public ChooseResult(String filePath, int requestCode) {
            this.filePath = filePath;
            this.requestCode = requestCode;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getRequestCode() {
            return requestCode;
        }
    }

}
