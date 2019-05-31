package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.utils.file.FileChooser;
import com.wtz.tools.view.TextureVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TextureVideoViewFragment extends Fragment {
    private static final String TAG = TextureVideoViewFragment.class.getSimpleName();

    private static final String VIDEO_PATH = "/sdcard/test.mp4";

    private TextureVideoView videoView;
    private SeekBar seekBar;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int mSelectRequestCode;
    private String mVideoPath = VIDEO_PATH;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseResult(FileChooser.ChooseResult chooseResult) {
        Log.d(TAG, "onResult requestCode=" + chooseResult.getRequestCode()
                + "; filePath=" + chooseResult.getFilePath());
        if (chooseResult.getRequestCode() == mSelectRequestCode) {
            mVideoPath = chooseResult.getFilePath();
            videoView.openVideo(mVideoPath);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_texture_video_view, container, false);

        view.findViewById(R.id.btn_select_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectRequestCode = FileChooser.chooseVideo(getActivity());
            }
        });

        final ImageButton playPause = view.findViewById(R.id.ib_play_pause);
        final TextView currentTime = view.findViewById(R.id.tv_current_time);
        final TextView totalTime = view.findViewById(R.id.tv_total_time);
        seekBar = view.findViewById(R.id.seek_bar);
        videoView = view.findViewById(R.id.video_view);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = videoView.getDuration();
                totalTime.setText(getTimeFormat(duration));
                seekBar.setMax(duration);
                playPause.setImageResource(R.drawable.pause);
                startTimeUpdate();
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setImageResource(R.drawable.play);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPause.setImageResource(R.drawable.play);
                } else {
                    videoView.start();
                    playPause.setImageResource(R.drawable.pause);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime.setText(getTimeFormat(progress));
            }
        });

        playPause.requestFocus();

        return view;
    }

    private String getTimeFormat(int time) {
        String timeFormat = "";
        int flag = time / 60000;
        if (flag < 10) {
            timeFormat = "0" + time / 60000;
        } else {
            timeFormat = "" + time / 60000;
        }
        flag = time % 60000 / 1000;
        if (flag < 10) {
            timeFormat += ":0" + flag;
        } else {
            timeFormat += ":" + flag;
        }
        return timeFormat;
    }

    private void startTimeUpdate() {
        mHandler.removeCallbacks(mUpdateTimeRunnable);
        mHandler.post(mUpdateTimeRunnable);
    }

    private void stopTimeUpdate() {
        mHandler.removeCallbacks(mUpdateTimeRunnable);
    }

    private Runnable mUpdateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (seekBar != null) {
                seekBar.setProgress(videoView.getCurrentPosition());
            }
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        videoView.setRotation(45.0f);
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
        stopTimeUpdate();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
