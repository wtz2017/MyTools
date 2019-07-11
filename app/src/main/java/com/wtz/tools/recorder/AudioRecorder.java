package com.wtz.tools.recorder;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class AudioRecorder {

    private MediaRecorder mMediaRecorder;
    private String mSaveDir;
    private String mCurrentFilePath;
    private boolean isPrepared;

    public AudioRecorder(String dir) {
        mSaveDir = dir;
    }

    /**
     * 准备
     */
    public void prepareAudio() {
        try {
            isPrepared = false;

            File dir = new File(mSaveDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            // 准备结束
            isPrepared = true;
            if (mListener != null) {
                mListener.onPrepareComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 生成UUID唯一标示符
    // 算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            //获得最大的振幅getMaxAmplitude() 1-32767
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            delete(mCurrentFilePath);
            mCurrentFilePath = null;
        }
    }

    public static void delete(String filePath) {
        if (filePath == null) return;
        File file = new File(filePath);
        file.delete();
    }

    public void release() {
        isPrepared = false;
        if (mMediaRecorder == null) {
            return;
        }
        // stop方法报"stop failed"错误的解决方案
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调录音准备完毕
     */
    public interface RecordStateListener {
        void onPrepareComplete();
    }

    public RecordStateListener mListener;

    public void setOnAudioStateListener(RecordStateListener listener) {
        mListener = listener;
    }

    /**
     * 得到amr的时长
     */
    public static long getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();//文件的长度
            int pos = 6;//设置初始位置
            int frameCount = 0;//初始帧数
            int packedPos = -1;

            byte[] datas = new byte[1];//初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            duration += frameCount * 20;//帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return duration;
    }

}
