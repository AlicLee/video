package com.example.lee.videoandroid.base;

import android.app.Application;

import com.example.lee.videoandroid.util.Utils;

public class VideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
