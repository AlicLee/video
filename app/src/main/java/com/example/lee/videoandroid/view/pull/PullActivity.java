package com.example.lee.videoandroid.view.pull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.bean.VideoijkBean;
import com.dou361.ijkplayer.listener.OnPlayerBackListener;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.util.PermissionUtils;
import com.example.lee.videoandroid.util.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PullActivity extends BaseActivity {
    //    private IjkVideoView ijkVideoView;
    private PlayerView playerView;
    private final String TAG = PullActivity.class.getSimpleName();

    @Override
    public int setLayoutView() {
        return R.layout.activity_pull;
    }

    @Override
    public void initView() {
        PermissionUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {

            }

            @Override
            public void forbitPermissons() {
                ToastUtils.showShortToast("您必须允许摄像头权限才可以继续");
                finish();
            }
        });
        Intent intent = getIntent();
        String beanData = intent.getStringExtra("beanData");
        String type = intent.getStringExtra("type");
        if (type == null) {
            initPlayView("").startPlay();
        } else if (type.equals("video")) {
            VideoBean bean = new Gson().fromJson(beanData, VideoBean.class);
            Log.e(TAG, "initView: " + Api.FILE_HOST + bean.getPath());
            initPlayView(bean.getName()).setPlaySource(Api.FILE_HOST + bean.getPath()).startPlay();
        } else if (type.equals("live")) {
            LiveBean bean = new Gson().fromJson(beanData, LiveBean.class);

//            List<VideoijkBean> beanList = new ArrayList<>();
//            VideoijkBean videoijkBean = new VideoijkBean();
//            videoijkBean.setStream("高清");
//            videoijkBean.setUrl(bean.getLiveHdAddress());
//            beanList.add(videoijkBean);
//
//            videoijkBean.setStream("标清");
//            videoijkBean.setUrl(bean.getLiveSdAddress());
//            beanList.add(videoijkBean);
//
//            videoijkBean.setStream("普清");
//            videoijkBean.setUrl(bean.getLiveLdAddress());
//            beanList.add(videoijkBean);
            initPlayView(bean.getLiveTitle()).setPlaySource(bean.getLiveAddress()).startPlay();
        }
//        if (bean.getLiveHdAddress() == null) {
//            playerView = new PlayerView(this).setTitle(bean.getLiveTitle())
//                    .setScaleType(PlayStateParams.fillparent)
//                    .forbidTouch(false)
//                    .hideMenu(true)
//                    .showThumbnail(new OnShowThumbnailListener() {
//                        @Override
//                        public void onShowThumbnail(ImageView ivThumbnail) {
//                            Glide.with(PullActivity.this)
//                                    .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                    .placeholder(R.color.DimGray)
//                                    .error(R.color.white)
//                                    .into(ivThumbnail);
//                        }
//                    }).startPlay();
//            ToastUtils.showShortToast("该用户还未直播");
//            return;
//        }
//        String url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        //标清sd 普清ld 高清hd

    }

    @Override
    public void initData() {

    }

    public PlayerView initPlayView(String title) {
        return playerView = new PlayerView(this)
                .setTitle(title)
                .setScaleType(PlayStateParams.fitparent)
                .forbidTouch(false)
                .hideMenu(true)
//                .showThumbnail(new OnShowThumbnailListener() {
//                    @Override
//                    public void onShowThumbnail(ImageView ivThumbnail) {
//                        Glide.with(PullActivity.this)
//                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                .placeholder(R.color.DimGray)
//                                .error(R.color.white)
//                                .into(ivThumbnail);
//                    }
//                })
                .setPlayerBackListener(new OnPlayerBackListener() {
                    @Override
                    public void onPlayerBack() {
                        //这里可以简单播放器点击返回键
                        finish();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null)
            playerView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView != null)
            playerView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerView != null)
            playerView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (playerView != null) {
            playerView.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (playerView != null && playerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
