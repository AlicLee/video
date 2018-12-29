package com.example.lee.videoandroid.network;

import rx.Subscriber;
import rx.Subscription;

public interface HttpTaskListener<T> {
    void onSuccess(T t);

    void onError(String message);
}
