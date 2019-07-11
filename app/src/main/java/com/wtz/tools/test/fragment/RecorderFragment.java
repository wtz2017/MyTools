package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wtz.tools.R;
import com.wtz.tools.recorder.AudioPlayer;
import com.wtz.tools.recorder.AudioRecorder;
import com.wtz.tools.recorder.AudioRecorderButton;
import com.wtz.tools.test.adapter.RecorderAdapter;
import com.wtz.tools.test.data.RecorderItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecorderFragment extends Fragment {
    private static final String TAG = RecorderFragment.class.getSimpleName();

    private ListView mListView;
    private ArrayAdapter<RecorderItem> mAdapter;
    private List<RecorderItem> mDatas = new ArrayList<>();

    private AudioRecorderButton mAudioRecorderButton;
    private View mItemPlayAnimView;
    private AnimationDrawable mItemPlayAnimation;
    
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_recorder, container, false);

        mAdapter = new RecorderAdapter(getActivity(), mDatas);

        mListView = view.findViewById(R.id.record_listview);
        mAudioRecorderButton = view.findViewById(R.id.id_recorder_button);

        initView();
        initData();

        return view;
    }

    private void initView() {
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 停止上一个条目的动画
                stopItemPlayAnim();

                // 播放动画
                mItemPlayAnimView = view.findViewById(R.id.id_recorder_anim);
                mItemPlayAnimView.setBackgroundResource(R.drawable.anim_play_sound);
                mItemPlayAnimation = (AnimationDrawable) mItemPlayAnimView.getBackground();
                mItemPlayAnimation.start();

                // 播放音频完成后改回原来的background
                AudioPlayer.playSound(mDatas.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopItemPlayAnim();
                    }
                });
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDeleteItemDialog(view.getContext(), position);
                return true;
            }
        });

        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                //每完成一次录音
                RecorderItem recorder = new RecorderItem(seconds, filePath);
                mDatas.add(recorder);
                //更新adapter
                mAdapter.notifyDataSetChanged();
                //设置listview 位置
                mListView.setSelection(mDatas.size() - 1);
            }
        });
    }

    private void showDeleteItemDialog(Context context, final int position) {
        final String path = mDatas.get(position).getFilePath();
        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setIcon(R.drawable.alert_circle)
                .setTitle("删除确认")
                .setMessage("确认要删除以下文件吗？\n" + path)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AudioRecorder.delete(path);
                        mDatas.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                }).create();
        dialog.show();
    }

    private void initData() {
        final File saveDir = new File(mAudioRecorderButton.getSaveDir());
        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = saveDir.listFiles();
                for (File file : files) {
                    float seconds = 0;
                    try {
                        seconds = AudioRecorder.getAmrDuration(file) / 1000;
                        if (seconds == 0) {
                            seconds = 1;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String filePath = file.getAbsolutePath();
                    RecorderItem recorder = new RecorderItem(seconds, filePath);
                    mDatas.add(recorder);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void stopItemPlayAnim() {
        if (mItemPlayAnimation != null) {
            mItemPlayAnimation.stop();
            mItemPlayAnimation = null;
        }
        if (mItemPlayAnimView != null) {
            mItemPlayAnimView.setBackgroundResource(R.drawable.voice_icon);
            mItemPlayAnimView = null;
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        AudioPlayer.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        AudioPlayer.pause();
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
        AudioPlayer.release();
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
