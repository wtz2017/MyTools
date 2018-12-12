package com.wtz.tools.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;


public class TextureVideoView extends TextureView implements TextureView.SurfaceTextureListener, MediaPlayerControl {
    private static final String TAG = TextureVideoView.class.getSimpleName();

    private Context mContext;
    private SurfaceTexture mSurfaceTexture = null;
    private MediaPlayer mMediaPlayer = null;

    private Uri mUri;
    private int mDuration;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private boolean mIsPrepared;
    private boolean mStartWhenPrepared;
    private int mSeekWhenPrepared;
    private int mCurrentBufferPercentage;

    private OnPreparedListener mOnPreparedListener;
    private OnInfoListener mOnInfoListener;
    private OnErrorListener mOnErrorListener;
    private OnCompletionListener mOnCompletionListener;


    public TextureVideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        Log.i(TAG, "onMeasure---defaultWidth:" + width + ",defaultHeight:" + height + ",mVideoWidth:" + mVideoWidth + ",mVideoHeight:" + mVideoHeight + ",widthMeasureSpec:" + widthMeasureSpec + ",heightMeasureSpec:" + heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                Log.i(TAG, "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            } else if (mVideoWidth * height < width * mVideoHeight) {
                Log.i(TAG, "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else {
                Log.i(TAG, "aspect ratio is correct: " + width + "/" + height + "=" + mVideoWidth + "/" + mVideoHeight);
            }
        }
        Log.i(TAG, "finally set measured size: " + width + 'x' + height);
        setMeasuredDimension(width, height);
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        setSurfaceTextureListener(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable: " + width + "x" + height);
        mSurfaceTexture = surface;
        openVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged: " + width + "x" + height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed");
        mSurfaceTexture = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureUpdated");
    }

    public void openVideo(String path) {
        Log.d(TAG, "setVideoPath: " + path);
        if (path == null) {
            return;
        }
        path = convertSpecialCharacters(path);
        openVideo(Uri.parse(path));
    }

    private String convertSpecialCharacters(String name) {
        if (name == null) {
            return null;
        }

        if (name.contains("%")) {
            name = name.replace("%", "%25");
        }
        if (name.contains("#")) {
            name = name.replace("#", "%23");
        }
        if (name.contains("?")) {
            name = name.replace("?", "%3F");
        }
        return name;
    }

    public void openVideo(Uri uri) {
        mUri = uri;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    private void openVideo() {
        if (mUri == null || mSurfaceTexture == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        mIsPrepared = false;
        mDuration = 0;
        mCurrentBufferPercentage = 0;

        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(mPreparedListener);
                mMediaPlayer.setOnInfoListener(mInfoListener);
                mMediaPlayer.setOnErrorListener(mErrorListener);
                mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
                mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            } else {
                mMediaPlayer.reset();
            }

            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.prepareAsync();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        } catch (Exception ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        }
    }

    private void setFixedSize(int width, int height) {
        boolean change = false;
        if (mVideoWidth != width) {
            change = true;
            mVideoWidth = width;
        }
        if (mVideoHeight != height) {
            change = true;
            mVideoHeight = height;
        }
        if (change) {
            requestLayout();
        }
    }

    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    Log.i(TAG, "onVideoSizeChanged: " + width + "x" + height
                            + ", mVideoWidth=" + mVideoWidth + ", mVideoHeight=" + mVideoHeight);
                    setFixedSize(mp.getVideoWidth(), mp.getVideoHeight());
                }
            };

    private OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "onPrepared");
            mIsPrepared = true;

            setFixedSize(mp.getVideoWidth(), mp.getVideoHeight());
            Log.i(TAG, "mVideoWidth:" + mVideoWidth + " mVideoHeight:" + mVideoHeight + " mSurfaceWidth:" + mSurfaceWidth + " mSurfaceHeight:" + mSurfaceHeight);

            Log.i(TAG, "mStartWhenPrepared:" + mStartWhenPrepared + " mSeekWhenPrepared:" + mSeekWhenPrepared);
            if (mSeekWhenPrepared != 0) {
                mMediaPlayer.seekTo(mSeekWhenPrepared);
                mSeekWhenPrepared = 0;
            }

            if (mStartWhenPrepared) {
                mMediaPlayer.start();
                mStartWhenPrepared = false;
            }

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
        }
    };

    private OnCompletionListener mCompletionListener =
            new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private OnInfoListener mInfoListener =
            new OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (mOnInfoListener != null) {
                        return mOnInfoListener.onInfo(mMediaPlayer, what, extra);
                    }
                    return false;
                }
            };

    private OnErrorListener mErrorListener =
            new OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

                    /* Otherwise, pop up an error dialog so the user knows that
                     * something bad has happened. Only try and pop up the dialog
                     * if we're attached to a window. When we're going away and no
                     * longer have a window, don't bother showing the user an error.
                     */
                    if (getWindowToken() != null) {
                        Resources r = mContext.getResources();
                        String message;

                        if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                            message = "视频是流式传输的，它的容器对于渐进式播放无效";
                        } else {
                            message = "未知错误";
                        }

                        new AlertDialog.Builder(mContext)
                                .setTitle("播放异常")
                                .setMessage(message)
                                .setPositiveButton("确认",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // If we get here, there is no onError listener, so
                                                // at least inform them that the video is over.
                                                if (mOnCompletionListener != null) {
                                                    mOnCompletionListener.onCompletion(mMediaPlayer);
                                                }
                                            }
                                        })
                                .setCancelable(false)
                                .show();
                    }
                    return true;
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setmInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void start() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            mStartWhenPrepared = false;
        } else {
            mStartWhenPrepared = true;
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        mStartWhenPrepared = false;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = 0;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    public void setVideoSize(int width, int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
