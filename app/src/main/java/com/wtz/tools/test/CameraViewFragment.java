package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.wtz.tools.DateTimeUtil;
import com.wtz.tools.R;
import com.wtz.tools.StorageUtil;
import com.wtz.tools.image.BitmapUtils;
import com.wtz.tools.view.CameraView;

import java.io.File;

public class CameraViewFragment extends Fragment {
    private static final String TAG = CameraViewFragment.class.getSimpleName();

    private boolean canSavePic;
    private String mSavePicDir;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        canSavePic = StorageUtil.isExternalMemoryAvailable();
        if (canSavePic) {
            mSavePicDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            StorageUtil.checkAndMkDirs(new File(mSavePicDir));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_camera_view, container, false);

        final CameraView cameraView = rootView.findViewById(R.id.camera_view);
        cameraView.setPictureCallback(new CameraView.TakePictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, int orientation, Camera camera) {
                Log.d(TAG, "onPictureTaken orientation=" + orientation);
                if (canSavePic) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bitmap = BitmapUtils.rotateBitmap(bitmap, orientation, false);
                    String filePath = mSavePicDir + File.separator + DateTimeUtil.getCurrentDateTime("yyyyMMdd_HHmmss") + ".png";
                    BitmapUtils.bitmapToFile(bitmap, filePath, true);
                    Log.d(TAG, "save picture to " + filePath);
                }
            }
        });

        ImageButton buttonTakePic = rootView.findViewById(R.id.ib_take_pic);
        buttonTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });

        ImageButton buttonReverse = rootView.findViewById(R.id.ib_camera_reverse);
        buttonReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.switchCamera();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
