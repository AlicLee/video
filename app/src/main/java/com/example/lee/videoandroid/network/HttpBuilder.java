package com.example.lee.videoandroid.network;

import android.content.Context;


import rx.Observable;
import rx.Subscription;

public interface HttpBuilder<T> {
    HttpBuilder setContext(Context context);
    HttpBuilder setObservable(Observable observable);
    Subscription create();
    HttpBuilder setCallBack(HttpTaskListener<T>listener);
    HttpBuilder isShowDialog(boolean isShow);
    HttpBuilder isAddCommonSubscription();
}
