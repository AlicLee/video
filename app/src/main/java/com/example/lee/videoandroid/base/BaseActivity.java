package com.example.lee.videoandroid.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.util.MD5Utils;
import com.example.lee.videoandroid.util.TUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {
    Unbinder btBinder;
    private CompositeSubscription mCompositeSubscription;
    protected Api api;
    public P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutView());
        btBinder = ButterKnife.bind(this);
        api = (Api) HttpUtils.getInstance().createRequest(Api.class);
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.mContext = this;
            mPresenter.api = api;
            mPresenter.setView(this);
        }
        initView();
        initData();
    }

    public abstract int setLayoutView();

    public abstract void initView();

    public abstract void initData();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (btBinder != null) {
            btBinder.unbind();
        }
        removeSubscription();
        if (mPresenter != null) {
            mPresenter.onDestory();
        }
        super.onDestroy();
    }

    /**
     * 添加观察者
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
     * 移除观察者
     */
    public void removeSubscription() {
        HttpUtils.getInstance().removeSubscription();
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
            this.mCompositeSubscription = null;
        }
    }
}
