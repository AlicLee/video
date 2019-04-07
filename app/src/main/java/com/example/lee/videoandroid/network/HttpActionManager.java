package com.example.lee.videoandroid.network;

import rx.Subscription;

public interface HttpActionManager {
    void add(String tag, Subscription subscription);

//    void remove(String tag);

    void cancel(String tag);

//    void cancelAll();
}
