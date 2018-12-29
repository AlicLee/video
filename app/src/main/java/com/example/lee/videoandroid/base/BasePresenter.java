package com.example.lee.videoandroid.base;

import android.content.Context;

import com.example.lee.videoandroid.network.Api;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class BasePresenter<T> {
    public Context mContext;
    public T mView;
    public Api api;
    protected Reference<T>mViewRef;
    public void setView(T v){
        this.mView=v;
        this.onStart();
        mViewRef=new WeakReference<T>(this.mView);
    }
    public void onStart(){

    }
    public void onDestory(){
        api=null;
        if(mView!=null){
            mViewRef.clear();
            mViewRef=null;
        }
    }
}
