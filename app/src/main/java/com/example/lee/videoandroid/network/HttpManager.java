package com.example.lee.videoandroid.network;

import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rx.Subscription;

public class HttpManager implements HttpActionManager {
    private static HttpManager instance = null;
    private HashMap<String, Subscription> maps;

    public static HttpManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    private HttpManager() {
        maps = new HashMap<>();
    }


    @Override
    public void add(String tag, Subscription subscription) {
        maps.put(tag, subscription);
    }

//    @Override
//    public void remove(String tag) {
//        if (!maps.isEmpty())
//            maps.remove(tag);
//    }

    @Override
    public void cancel(String tag) {
        if (maps.isEmpty()) return;
        if (maps.get(tag) == null) return;
        if (!maps.get(tag).isUnsubscribed()) {
            maps.get(tag).unsubscribe();
            maps.remove(tag);
        }
    }

//    @Override
//    public void cancelAll() {
//        Set<Map.Entry<String, Subscription>> subscriptionMap = maps.entrySet();
//        for(int i=0;i<subscriptionMap.size();i++){
//            subscriptionMap.iterator()
//        }
////        for(int i=0;)
//    }
}
