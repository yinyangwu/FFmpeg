package com.youngwu.ffmpeg;

import android.view.Surface;

/**
 * Desc:FFmpeg工具类
 * <p>
 * Created by YoungWu on 2019-08-29.
 */
public class FFmpegUtil {

    static {
        System.loadLibrary("ffmpeg-lib");
    }

    public static native String urlProtocolInfo();

    public static native String avFormatInfo();

    public static native String avCodecInfo();

    public static native String avFilterInfo();

    public static native void playVideo(String videoPath, Surface surface);
}
