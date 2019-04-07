package com.example.lee.videoandroid.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.util.TUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseFragment<P extends BasePresenter> extends Fragment {
    Unbinder btBinder;
    Api api;
    public P mPresenter;
    private CompositeSubscription mCompositeSubscription;
    protected Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = (Api) HttpUtils.getInstance().createRequest(Api.class);
        mPresenter = TUtil.getT(this, 0);
        mContext = getActivity();
        if (mPresenter != null) {
            mPresenter.api = api;
            mPresenter.mContext = mContext;
            mPresenter.setView(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(setLayoutView(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btBinder = ButterKnife.bind(this, view);
        initData(view);
        initView(view);
    }

    public abstract void initData(View view);

    public abstract void initView(View view);

    public abstract int setLayoutView();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected Unbinder getBtBinder() {
        return btBinder;
    }

    @Override
    public void onDestroyView() {
        removeSubscription();
        if (btBinder != null) {
            btBinder.unbind();
        }
        super.onDestroyView();
    }

    /**
     * 添加网络请求观察者
     *
     * @param s
     */
    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    /**
     * 移除网络请求
     */
    public void removeSubscription() {
        HttpUtils.getInstance().removeSubscription();
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
            this.mCompositeSubscription = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
