package com.example.lee.videoandroid.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoUtil {
    public static File getVideoThumb(String path) {
        File file = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
            Calendar now = new GregorianCalendar();
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String fileName = simpleDate.format(now.getTime());
            file = new File(Environment.getExternalStorageDirectory() + fileName + ".png");
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mediaMetadataRetriever.release();
        }
        return file;
    }

//    public static HashMap<String, Object> getVideoInfo(String path) {
//        HashMap<String, Object> videoInfo = new HashMap();
//        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//        try {
//            HashMap<String, String> params = new HashMap<>();
//
//            mediaMetadataRetriever.setDataSource(path, params);
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
//            String duration = mediaMetadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
//            videoInfo.put("thumb", bitmap);
//            videoInfo.put("duration", duration);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("VideoUtil", "MediaMetadataRetriever:" + e.toString() + "path:" + path);
//        } finally {
//            mediaMetadataRetriever.release();
//        }
//        return videoInfo;
//    }
}
