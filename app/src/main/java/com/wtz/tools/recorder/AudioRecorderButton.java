package com.wtz.tools.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.wtz.tools.R;

@SuppressLint("AppCompatCustomView")
public class AudioRecorderButton extends Button implements AudioRecorder.RecordStateListener {
    private static final String TAG = AudioRecorderButton.class.getSimpleName();

    // 手指滑动 距离
    private static final int DISTANCE_Y_CANCEL = 50;

    // UI状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private int mCurrentUiState = STATE_NORMAL;

    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private RecordDialog mRecordDialog;
    private AudioRecorder mAudioRecorder;

    // 是否触发 longClick
    private boolean mReady;
    // 已经开始录音
    private boolean isRecording = false;
    // 录音时间
    private float mTime;

    private String mSaveDir;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRecordDialog = new RecordDialog(getContext());
        // todo 没有判断 是否存在，是否可读。
        mSaveDir = context.getApplicationContext().getExternalFilesDir("recorder_audios").getAbsolutePath();
        Log.d(TAG, "save audio mSaveDir: " + mSaveDir);
        mAudioRecorder = new AudioRecorder(mSaveDir);
        mAudioRecorder.setOnAudioStateListener(this);
        // 按钮长按 准备录音 包括start
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "OnLongClick");
                mReady = true;
                mRecordDialog.showRecordingDialog();
                mAudioRecorder.prepareAudio();
                return false;
            }
        });
    }

    @Override
    public void onPrepareComplete() {
        Log.d(TAG, "onPrepareComplete");
//        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
        // 不能在消息发送后置状态，否则在短按时会出现先收到touch抬起回调和reset，
        // 而后触发消息又把isRecording置true的问题
        isRecording = true;
        new Thread(mGetVoiceLevelRunnable).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOICE_CHANGED:
                    mRecordDialog.updateVoiceLevel(mAudioRecorder.getVoiceLevel(7));
                    break;

                case MSG_DIALOG_DIMISS:
                    mRecordDialog.dimissDialog();
                    break;
            }
        }
    };

    // 获取音量大小的Runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1;
//                    Log.d(TAG, "mTime=" + mTime + ",isRecording=" + isRecording);
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent ACTION_DOWN");
                changeUiState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据想x,y的坐标，判断是否想要取消
                    if (wantToCancel(x, y)) {
                        changeUiState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeUiState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent ACTION_UP mReady=" + mReady + ",isRecording=" + isRecording);
                if (!mReady) {
                    // 如果 longClick 没触发
                    mAudioRecorder.release();// 避免未结束录音
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording) {
                    handleTooShort();
                } else {
                    if (mTime < 0.6f) {
                        handleTooShort();
                    } else if (mCurrentUiState == STATE_RECORDING) {// 正常录制结束
                        mRecordDialog.dimissDialog();
                        isRecording = false;// 解决时间计算过长问题
                        mAudioRecorder.release();
                        Log.d(TAG, "onTouchEvent after release mTime=" + mTime);
                        if (mListener != null) {
                            mListener.onFinish(mTime, mAudioRecorder.getCurrentFilePath());
                        }
                    } else if (mCurrentUiState == STATE_WANT_TO_CANCEL) {
                        mRecordDialog.dimissDialog();
                        mAudioRecorder.cancel();
                    }
                }

                reset();

                break;

        }
        return super.onTouchEvent(event);
    }

    private void handleTooShort() {
        Log.d(TAG, "handleTooShort");
        mRecordDialog.tooShort();
        mAudioRecorder.cancel();
        mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);
    }

    /**
     * 恢复状态 标志位
     */
    private void reset() {
        Log.d(TAG, "reset");
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeUiState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        //如果左右滑出 button
        if (x < 0 || x > getWidth()) {
            return true;
        }
        //如果上下滑出 button  加上我们自定义的距离
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void changeUiState(int state) {
        if (mCurrentUiState != state) {
            mCurrentUiState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.button_not_record);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mRecordDialog.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_want_cancel);
                    mRecordDialog.wantToCancel();
                    break;
            }
        }
    }

    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    public String getSaveDir() {
        return mSaveDir;
    }
}