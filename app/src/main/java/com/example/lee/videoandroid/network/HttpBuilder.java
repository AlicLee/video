package com.example.lee.videoandroid.network;

import android.content.Context;
import android.support.annotation.NonNull;


import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

public interface HttpBuilder<T> {
    HttpBuilder setContext(Context context);

    HttpBuilder create(Observable observable);

    HttpBuilder create(Call call);

    HttpBuilder setCallBack(HttpTaskListener<T> listener);

    HttpBuilder isShowDialog(boolean isShow);

    HttpBuilder isAddCommonSubscription();
}
