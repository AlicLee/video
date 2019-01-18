package com.example.lee.videoandroid.view.push;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.livesdk.SrsCameraView;
import com.example.lee.livesdk.SrsEncodeHandler;
import com.example.lee.livesdk.SrsHandlerListener;
import com.example.lee.livesdk.SrsPublisher;
import com.example.lee.livesdk.SrsRecordHandler;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class PushActivity extends BaseActivity {
    @BindView(R.id.left_btn)
    ImageView leftBtn;
    @BindView(R.id.push_title)
    TextView pushTitle;
    @BindView(R.id.push_pause_btn)
    ImageView pushPauseBtn;
    //    @BindView(R.id.push_refresh)
//    ImageView pushRefresh;
    @BindView(R.id.push_bottom_layout)
    RelativeLayout pushBottomLayout;
    @BindView(R.id.preview)
    SrsCameraView preview;
    private final String TAG = PushActivity.class.getSimpleName();
    @BindView(R.id.push_title_layout)
    RelativeLayout pushTitleLayout;
    @BindView(R.id.phone_orientation)
    ImageView phoneOrientation;
    @BindView(R.id.switch_camera)
    ImageView switchCamera;

    private String rtmpUrl = "rtmp://192.168.103.111:1935/live/";
    private SrsPublisher mPublisher;
    private boolean isStartPush = false;
    private int control_view_delay = 5000;
    private DisMissControllerTimerTask control_view_timerTask = null;
    private Timer control_view_timer = null;

    @Override
    public int setLayoutView() {
        return R.layout.activity_push;
    }

    @Override
    public void initView() {
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void initData() {
        String userBeanString = SharedPreUtil.getString(this, Settings.SharedPreUserKey, "");
        if (userBeanString.length() == 0) {
            ToastUtils.showShortToast("用户未登陆");
            return;
        }
        UserBean userBean = new Gson().fromJson(userBeanString, UserBean.class);
        rtmpUrl = rtmpUrl + userBean.getId() + "/test";
        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.preview));
        mPublisher.setEncodeHandler(new SrsEncodeHandler(new SrsHandlerListener() {
            @Override
            public void onNetworkWeak() {

            }

            @Override
            public void onNetworkResume() {

            }

            @Override
            public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

            }
        }));
        mPublisher.setRtmpHandler(new RtmpHandler(new RtmpHandler.RtmpListener() {
            @Override
            public void onRtmpConnecting(String msg) {

            }

            @Override
            public void onRtmpConnected(String msg) {

            }

            @Override
            public void onRtmpVideoStreaming() {

            }

            @Override
            public void onRtmpAudioStreaming() {

            }

            @Override
            public void onRtmpStopped() {

            }

            @Override
            public void onRtmpDisconnected() {

            }

            @Override
            public void onRtmpVideoFpsChanged(double fps) {

            }

            @Override
            public void onRtmpVideoBitrateChanged(double bitrate) {

            }

            @Override
            public void onRtmpAudioBitrateChanged(double bitrate) {

            }

            @Override
            public void onRtmpSocketException(SocketException e) {

            }

            @Override
            public void onRtmpIOException(IOException e) {

            }

            @Override
            public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

            }

            @Override
            public void onRtmpIllegalStateException(IllegalStateException e) {

            }
        }));
        mPublisher.setRecordHandler(new SrsRecordHandler(new SrsRecordHandler.SrsRecordListener() {
            @Override
            public void onRecordPause() {

            }

            @Override
            public void onRecordResume() {

            }

            @Override
            public void onRecordStarted(String msg) {

            }

            @Override
            public void onRecordFinished(String msg) {

            }

            @Override
            public void onRecordIllegalArgumentException(IllegalArgumentException e) {

            }

            @Override
            public void onRecordIOException(IOException e) {

            }
        }));
        int PREVIEW_HEIGHT = 360, PREVIEW_WIDTH = 640;
        try {
            mPublisher.setPreviewResolution(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            mPublisher.setOutputResolution(PREVIEW_HEIGHT, PREVIEW_WIDTH);
            mPublisher.setVideoHDMode();
            mPublisher.startCamera();
        } catch (Exception e) {
            ToastUtils.showShortToast("打开相机错误,请检查是否其他应用程序开启相机了");
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
//        btnRecord.setText("record");
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (isStartPush) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    public void startPush() {
        mPublisher.startPublish(rtmpUrl);
        mPublisher.startCamera();
        pushPauseBtn.setImageResource(R.drawable.pause);
        phoneOrientation.setVisibility(View.INVISIBLE);
        isStartPush = true;
    }

    public void stopPush() {
        mPublisher.stopPublish();
        mPublisher.stopRecord();
        pushPauseBtn.setImageResource(R.drawable.play);
        phoneOrientation.setVisibility(View.VISIBLE);
        isStartPush = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDissmissControlView();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }


    @OnClick({R.id.left_btn, R.id.push_pause_btn, R.id.phone_orientation, R.id.preview, R.id.switch_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.push_pause_btn:
                if (isStartPush)
                    stopPush();
                else
                    startPush();
                break;
            case R.id.phone_orientation:
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.preview:
                if (control_view_timer == null) {
                    setControlViewVisible(View.VISIBLE);
                    startDissmissControlView();
                }
                break;
            case R.id.switch_camera:
                mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
                break;
        }
    }

    public void startDissmissControlView() {
        if (control_view_timer == null) {
            control_view_timer = new Timer();
            control_view_timerTask = new DisMissControllerTimerTask();
            control_view_timer.schedule(control_view_timerTask, control_view_delay);
        }
    }

    public void cancelDissmissControlView() {
        if (control_view_timerTask != null) {
            control_view_timerTask.cancel();
            control_view_timerTask = null;
        }
        if (control_view_timer != null) {
            control_view_timer.cancel();
            control_view_timer = null;
        }
    }

    public void setControlViewVisible(int visibleIndex) {
        pushBottomLayout.setVisibility(visibleIndex);
        pushTitleLayout.setVisibility(visibleIndex);
    }


    public class DisMissControllerTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setControlViewVisible(View.INVISIBLE);
                    cancelDissmissControlView();
                }
            });
        }
    }
}
