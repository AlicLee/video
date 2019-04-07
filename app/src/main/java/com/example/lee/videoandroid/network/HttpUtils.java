package com.example.lee.videoandroid.network;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class HttpUtils<T> {
    private volatile Object httpTask;
    private static HashMap<String, Object> customHttpMaps = new HashMap<>();
    private CompositeSubscription mCompositeSubscription; //网络管理器
    ProgressBar bar = null;
    public static final String JSON_CONTENT_TYPE = "application/json", MULT_PART_TYPE = "multipart/form-data";

    /**
     * 获取单例
     *
     * @return
     */
    public static HttpUtils getInstance() {
        return httpBuilder.instance;
    }

    /**
     * 单例
     */
    private static class httpBuilder {
        private static final HttpUtils instance = new HttpUtils();
    }

    private Retrofit.Builder getBuilder(String apiUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(getOkClient());
        builder.baseUrl(apiUrl);//设置baseurl
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        return builder;
    }

    private OkHttpClient getOkClient() {
        return new OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    /**
     * 一般请求
     *
     * @param <T>
     * @param a
     * @return
     */
    public <T> T createRequest(Class<T> a) {
        if (httpTask == null) {
            synchronized (HttpUtils.class) {
                if (httpTask == null) {
                    httpTask = getBuilder(Api.HOST).build().create(a);
                }
            }
        }
        return (T) httpTask;
    }

    /**
     * 自定义请求服务
     *
     * @param a
     * @param <T>
     * @return
     */
    public <T> T createRequest(Class a, String baseUrl) {
        if (!customHttpMaps.containsKey(baseUrl)) {
            customHttpMaps.put(baseUrl, getBuilder(baseUrl).build().create(a));
        }
        return (T) customHttpMaps.get(baseUrl);
    }

    /**
     * 添加网络请求观察者
     *
     * @param s
     */
    public void addSubscription(Subscription s) {
        if (s == null) {
            return;
        }
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    /**
     * 移除网络请求
     */
    public void removeSubscription() {
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
            this.mCompositeSubscription = null;
        }
    }

    public ProgressBar showProgressBar(Context context) {
        if (bar == null)
            bar = new ProgressBar(context);
        bar.setVisibility(View.VISIBLE);
        return bar;
    }

    public void closeProgressBar(Context context) {
        if (bar != null) {
            bar.setVisibility(View.GONE);
            bar = null;
        }
    }


}
