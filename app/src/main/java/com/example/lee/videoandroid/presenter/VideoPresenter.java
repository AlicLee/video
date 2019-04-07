package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.VideoContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.view.main.VideoFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class VideoPresenter extends BasePresenter<VideoFragment> implements VideoContact.Presenter {
    @Override
    public void getVideoByPages(int pageIndex, int pageSize) {
        getVideoByPages(pageIndex, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<VideoBean> videoBeanList = new Gson().fromJson(o.toString(), new TypeToken<List<VideoBean>>() {
                }.getType());
                mView.getVideoByPagesSuccess(videoBeanList);
            }

            @Override
            public void onError(String message) {
                mView.getVideoByPagesFailure(message);
            }
        });
    }

    @Override
    public void refresh(int pageIndex, int pageSize) {
        getVideoByPages(pageIndex, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<VideoBean> videoBeanList = new Gson().fromJson(o.toString(), new TypeToken<List<VideoBean>>() {
                }.getType());
                mView.onRefreshSuccess(videoBeanList);
            }

            @Override
            public void onError(String message) {
                mView.onRefreshFailure(message);
            }
        });
    }

    @Override
    public void loadMore(int pageIndex, int pageSize) {
        getVideoByPages(pageIndex, pageSize, new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                List<VideoBean> videoBeanList = new Gson().fromJson(o.toString(), new TypeToken<List<VideoBean>>() {
                }.getType());
                mView.onLoadMoreSuccess(videoBeanList);
            }

            @Override
            public void onError(String message) {
                mView.onLoadMoreFailure(message);
            }
        });
    }

    public void getVideoByPages(int pageIndex, int pageSize, HttpTaskListener listener) {
        HttpPresenter.getInstance().setContext(mContext).setCallBack(listener).create(api.getVideoByPages(pageIndex, pageSize));
    }
}
