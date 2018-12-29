package com.example.lee.livesdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;



import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends Activity implements SurfaceHolder.Callback,IjkMediaPlayer.OnErrorListener,IMediaPlayer.OnCompletionListener {
    private SurfaceView surfaceView;
    private Button button;
    private EditText editView;
    private SurfaceHolder holder;
    /**
     * 由ijkplayer提供,用于播放视频
     */
    private IMediaPlayer mMediaPlayer=null;
    private String mPath="";
    private final String TAG=PlayerActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        new JSONObject().optString("123");
    }

    private void initView() {
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        button=(Button)findViewById(R.id.button);
        editView=(EditText)findViewById(R.id.editView);
        holder=surfaceView.getHolder();
        holder.addCallback(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPath=editView.getText().toString();
                if(prepare())
                    start();
            }
        });
    }
    public boolean prepare(){
        mMediaPlayer=new IjkMediaPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
            if(holder==null) {
                Log.d(TAG, "prepare: holder为空");
                return false;
            }
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void start(){
        if(mMediaPlayer==null){
            prepare();
        }
        mMediaPlayer.start();
    }
    public void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder=holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.holder=holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

}
