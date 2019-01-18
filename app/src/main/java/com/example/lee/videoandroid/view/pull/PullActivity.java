package com.example.lee.videoandroid.view.pull;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.listener.OnPlayerBackListener;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;

public class PullActivity extends BaseActivity {
    //    private IjkVideoView ijkVideoView;
    private PlayerView playerView;

    @Override
    public int setLayoutView() {
        return R.layout.activity_pull;
    }

    @Override
    public void initView() {
        String url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        playerView = new PlayerView(this)
                .setTitle("什么")
                .setScaleType(PlayStateParams.fitparent)
                .forbidTouch(false)
                .hideMenu(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(PullActivity.this)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                .placeholder(R.color.DimGray)
                                .error(R.color.white)
                                .into(ivThumbnail);
                    }
                })
                .setPlaySource(url)
                .setPlayerBackListener(new OnPlayerBackListener() {
                    @Override
                    public void onPlayerBack() {
                        //这里可以简单播放器点击返回键
                        finish();
                    }
                })
                .startPlay();
    }

    @Override
    public void initData() {

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
