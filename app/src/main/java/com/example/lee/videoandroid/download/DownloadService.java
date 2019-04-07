package com.example.lee.videoandroid.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.ArrayMap;
import android.util.Log;

import com.example.lee.videoandroid.util.LogUtil;
import com.example.lee.videoandroid.util.SharedPreUtil;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();

    public static final int HANDLE_DOWNLOAD = 0x001;
    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url", BUNDLE_KEY_DOWNLOAD_NAME = "download_file_name";
    public static final float UNBIND_SERVICE = 2.0F;

    private Activity activity;
    private DownloadBinder binder;
    private DownloadManager downloadManager;
    private DownloadChangeObserver downloadObserver;
    private BroadcastReceiver downLoadBroadcast;
    private ScheduledExecutorService scheduledExecutorService;

    //下载任务ID
    private long downloadId;
    private String downloadUrl;
    public static OnProgressListener onProgressListener;
//    public static HashMap<String, Long> downloadMap = new HashMap();
    @SuppressLint("HandlerLeak")
    public Handler downLoadHandler = new Handler() { //主线程的handler
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (onProgressListener != null && HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    onProgressListener.onProgress(msg.arg1 / (float) msg.arg2);
                }
            }
        }
    };

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: invoke DownloadService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("videoDownload", "下载视频", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "videoDownload")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();
            startForeground(101, notification);
        }
        binder = new DownloadBinder();
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: invoke DownloadService");
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        String fileName = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_NAME);
        downloadApk(downloadUrl, fileName);
        return binder;
    }

    /**
     * 下载最新APK
     */
    private void downloadApk(String url, String name) {
        downloadObserver = new DownloadChangeObserver();
        registerContentObserver();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "my.apk");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_MOVIES, name);
        request.setTitle("title");
        //request.setDescription("下载中通知栏提示");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType("application/com.lsp.download.file"); //用于响应点击的打开文件
        request.setVisibleInDownloadsUi(true);  //显示下载界面
        request.allowScanningByMediaScanner();  //准许被系统扫描到
        downloadId = downloadManager.enqueue(request);
//        downloadMap.put(url,downloadId);
        registerBroadcast(); //下载成功和点击通知栏动作监听
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(downLoadBroadcast = new DownLoadBroadcast(), intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast);
            downLoadBroadcast = null;
        }
    }

    /**
     * 注册ContentObserver
     */
    private void registerContentObserver() {
        /** observer download change **/
        if (downloadObserver != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://downloads/my_downloads"), false, downloadObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadObserver != null) {
            getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * 关闭定时器，线程等操作
     */
    private void close() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }

        if (downLoadHandler != null) {
            downLoadHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 发送Handler消息更新进度和状态
     * 将查询结果从子线程中发往主线程（handler方式），以防止ANR
     */
    private void updateProgress() {
        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        downLoadHandler.sendMessage(downLoadHandler.obtainMessage(HANDLE_DOWNLOAD, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
            switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {

                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.e(TAG, "download Status: STATUS_SUCCESSFUL");
                    break;
                case DownloadManager.STATUS_PAUSED:
                    Log.e(TAG, "download Status: STATUS_PAUSED");
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Log.e(TAG, "download Status: STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.e(TAG, "download Status: STATUS_FAILED ");
                    break;
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 绑定此DownloadService的Activity实例
     *
     * @param activity
     */
    public void setTargetActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 接受下载完成广播
     */
    private class DownLoadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    if (downloadId == downId && downId != -1 && downloadManager != null) {
                        Uri downIdUri = downloadManager.getUriForDownloadedFile(downloadId);
                        close();
                        if (downIdUri != null) {
//                            SPTools.put(Constant.SP_DOWNLOAD_PATH, downIdUri.getPath());
//                            LogUtil.showLog(TAG, "广播监听下载完成，APK存储路径为 ：" + downIdUri.getPath());
                            Log.e(TAG, "广播监听下载完成,Apk存储路径为:" + downIdUri.getPath());
                            installApk(context, downIdUri);
                        }
                        if (onProgressListener != null) {
                            onProgressListener.onProgress(UNBIND_SERVICE);
                        }
                    }
                    break;

                case DownloadManager.ACTION_NOTIFICATION_CLICKED:

                    break;
            }
        }
    }

    /**
     * 监听下载进度
     */
    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(downLoadHandler);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        @Override
        public void onChange(boolean selfChange) {
            scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS); //在子线程中查询
        }
    }

    public class DownloadBinder extends Binder {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        public DownloadService getService() {
            return DownloadService.this;
        }

    }

    public interface OnProgressListener {
        /**
         * 下载进度
         *
         * @param fraction 已下载/总大小
         */
        void onProgress(float fraction);
    }

    /**
     * 对外开放的方法
     *
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        DownloadService.onProgressListener = onProgressListener;
    }

    public static void installApk(Context context, Uri apkPath) {
        Log.e(TAG, "installApkPathComplete:" + apkPath);
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(apkPath, "application/vnd.android.package-archive");
//        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: invoke downloadSerice");
        unregisterBroadcast();
        unregisterContentObserver();
    }
}
