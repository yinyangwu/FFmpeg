package com.youngwu.ffmpeg;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Desc:FFmpeg视频播放视图
 * <p>
 * Created by YoungWu on 2019-08-29.
 */
public class FFmpegVideoView extends SurfaceView {
    private Surface mSurface;

    public FFmpegVideoView(Context context) {
        super(context);
        init();
    }

    public FFmpegVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FFmpegVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.RGBA_8888);
        mSurface = holder.getSurface();
    }

    public void playVideo(final String videoPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegUtil.playVideo(videoPath, mSurface);
            }
        }).start();
    }
}
