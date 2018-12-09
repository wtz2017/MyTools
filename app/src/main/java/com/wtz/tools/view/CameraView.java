package com.wtz.tools.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraView.class.getSimpleName();

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraCount;
    private int[] mCameraIds;
    private boolean canAutoFocus;

    private int mDisplayOrientation;
    private int mScreenWidth;
    private int mScreenHeight;
    private Camera.Size mPictureSize;
    private Camera.Size mPreviewSize;

    private boolean isPreview;
    private int mCurrentCamera;

    private static final int MSG_AUTO_FOCUS = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_FOCUS:
                    mHandler.removeMessages(MSG_AUTO_FOCUS);
                    if (isPreview) {
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Log.d(TAG, "mCamera onAutoFocus success=" + success);
                            }
                        });
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 3000);
                    break;
            }
        }
    };

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mCameraCount = Camera.getNumberOfCameras();
        mCameraIds = new int[mCameraCount];
        for (int i = 0; i < mCameraCount; i++) {
            mCameraIds[i] = i;
        }

        canAutoFocus = context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_AUTOFOCUS);

        mDisplayOrientation = getDisplayOrientation(mContext);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        Log.d(TAG, "mScreenWidth=" + mScreenWidth + ",mScreenHeight=" + mScreenHeight);

        mHolder = this.getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(this);
    }

    public void switchCamera() {
        mCurrentCamera++;
        if (mCurrentCamera >= mCameraCount) {
            mCurrentCamera = 0;
        }

        try {
            isPreview = false;
            mCamera.stopPreview();
            mCamera.release();

            mCamera = Camera.open(mCurrentCamera);
            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera = Camera.open(mCurrentCamera);
            if (mCamera == null) {
                return;
            }

            startPreview();
            mHandler.sendEmptyMessage(MSG_AUTO_FOCUS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() throws IOException {
        mCamera.setDisplayOrientation(mDisplayOrientation);
        mCamera.setParameters(getCameraParams());
        mCamera.setPreviewDisplay(mHolder);
        mCamera.startPreview();
        isPreview = true;
    }

    private int getDisplayOrientation(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);

        int result = (cameraInfo.orientation - degrees + 360) % 360;
        Log.d(TAG, "getDisplayOrientation: rotation=" + rotation + ", degrees=" + degrees
        + ", cameraInfo.orientation=" + cameraInfo.orientation + ", result=" + result);

        return result;
    }

    private Camera.Parameters getCameraParams() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null) {
            return parameters;
        }

        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
            }
        }

        float screenRatio = (float) mScreenWidth / mScreenHeight;
        Log.d(TAG, "screen display Ratio w/h:" + screenRatio);
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        mPreviewSize = getPreviewSize(supportedPreviewSizes, screenRatio);
        Log.d(TAG, "mPreviewSize:" + mPreviewSize.width + "," + mPreviewSize.height);

        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        mPictureSize = getPictureSize(supportedPictureSizes, mPreviewSize);
        Log.d(TAG, "mPictureSize:" + mPictureSize.width + "," + mPictureSize.height);

        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        if (canAutoFocus) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        return parameters;
    }

    private Camera.Size getPreviewSize(List<Camera.Size> sizes, float percent) {
        int best_index = 0;
        int best_width = 0;
        float best_diff = 100.0f;
        int listSize = sizes.size();
        for (int i = 0; i < listSize; i++) {
            int w = sizes.get(i).width;
            int h = sizes.get(i).height;
            Log.d(TAG, "supportedPreviewSizes: " + w + "x" +h);
            if (w * h > mScreenHeight * mScreenWidth)
                continue;

            float previewPercent = (float) w / h;
            float diff = Math.abs(previewPercent - percent);
            if (diff < best_diff || (diff == best_diff && w > best_width)) {
                best_index = i;
                best_diff = diff;
                best_width = w;
            }
        }
        return sizes.get(best_index);
    }

    private Camera.Size getPictureSize(List<Camera.Size> supportedPictureSizes, Camera.Size previewSize) {
        Camera.Size size = null;
        if (supportedPictureSizes != null && !supportedPictureSizes.isEmpty()) {
            for (Camera.Size picSize : supportedPictureSizes) {
                if (picSize.width * picSize.height > mScreenWidth * mScreenHeight)
                    continue;
                Log.d(TAG, "supportedPictureSizes: " + picSize.width + "x" + picSize.height);
                if (previewSize.width == picSize.width && previewSize.height == picSize.height) {
                    size = picSize;
                    break;
                }
            }
        }

        if (size != null) {
            return size;
        }

        for (Camera.Size picSize : supportedPictureSizes) {
            if (picSize.width * picSize.height > mScreenWidth * mScreenHeight)
                continue;
            if (picSize.width >= previewSize.width && picSize.height >= previewSize.width) {
                if (size == null) {
                    size = picSize;
                } else if (picSize.width * picSize.height > size.width * size.height) {
                    size = picSize;
                }
            }
        }

        return size;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged..." + width + "x" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
        isPreview = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // 控制拍照声音
        }
    };

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            isPreview = false;
            mCamera.stopPreview();

            if (mJpegCallback != null) {
                // 处理保存数据的旋转方向
                int orientation = mDisplayOrientation;
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCurrentCamera, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    orientation = -orientation;
                }
                mJpegCallback.onPictureTaken(data, orientation, camera);
            }

            mCamera.startPreview();
            isPreview = true;
        }
    };

    private TakePictureCallback mJpegCallback;

    public void setPictureCallback(TakePictureCallback jpegCallback) {
        mJpegCallback = jpegCallback;
    }

    public void takePicture() {
        if (mCamera != null && isPreview) {
            mCamera.takePicture(mShutterCallback, null, mPictureCallback);
        }
    }

    public interface TakePictureCallback {
        /**
         *
         * @param data
         * @param orientation 0/90/180/270/-90...
         * @param camera
         */
        void onPictureTaken(byte[] data, int orientation, Camera camera);
    }

}
