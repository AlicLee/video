package com.example.lee.livesdk;

import android.content.res.Configuration;

public class SrsConfig {
    //帧率,不能低于24帧,否则观看会卡顿。
    public static int VFPS=24;
    public static final String VCODEC = "video/avc";
    public static final String ACODEC = "audio/mp4a-latm";
    public static String x264Preset = "veryfast";
    public static int vBitrate = 1200 * 1024;  // 1200 kbps
    public static final int VGOP = 48;//i帧的间隔时间
    public static int previewOrientation= Configuration.ORIENTATION_PORTRAIT;
}
