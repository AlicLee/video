package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.HomeContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.view.main.HomeFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class HomePresenter extends BasePresenter<HomeFragment> implements HomeContact.Presenter {
//    private HomeFragment fragment;

    @Override
    public void setView(HomeFragment v) {
        super.setView(v);
//        this.fragment = v;
    }

    /**
     * 加载更多
     * @param index
     * @param pageSize
     */
    @Override
    public void loadMore(int index, int pageSize) {
        getLiveByPages(index, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<LiveBean> liveBeans = new Gson().fromJson(o.toString(), new TypeToken<List<LiveBean>>() {
                }.getType());
                mView.loadMoreSuccess(liveBeans);
            }

            @Override
            public void onError(String message) {
                mView.loadMoreFailure(message);
            }
        });
    }

    /**
     * 刷新获取数据
     * @param index
     * @param pageSize
     */
    @Override
    public void refresh(int index, int pageSize) {
        getLiveByPages(index, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<LiveBean> liveBeans = new Gson().fromJson(o.toString(), new TypeToken<List<LiveBean>>() {
                }.getType());
                mView.refreshSuccess(liveBeans);
            }

            @Override
            public void onError(String message) {
                mView.refreshFailure(message);
            }
        });
    }

    /**
     * 该方法只适用于第一次加载和第一次加载失败时。
     * @param index
     * @param pageSize
     */
    @Override
    public void getLiveByPages(int index, int pageSize) {
        getLiveByPages(index, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<LiveBean> liveBeans = new Gson().fromJson(o.toString(), new TypeToken<List<LiveBean>>() {
                }.getType());
                mView.getLiveByPagesSuccess(liveBeans);
            }

            @Override
            public void onError(String message) {
                mView.getLiveByPagesFailure(message);
            }
        });
    }

    /**
     * 根据需要订制返回数据
     *
     * @param index
     * @param pageSize
     * @param listener
     */
    public void getLiveByPages(int index, int pageSize, HttpTaskListener listener) {
        HttpPresenter.getInstance().setContext(mView.getActivity()).setCallBack(listener).create(api.requestLiveByPages(index, pageSize));
    }
}
