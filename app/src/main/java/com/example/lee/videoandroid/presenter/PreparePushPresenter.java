package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.PreparePushContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.push.PreparePushActivity;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PreparePushPresenter extends BasePresenter<PreparePushActivity> implements PreparePushContact.Presenter {
    @Override
    public void preparePush(LiveBean bean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(bean));
        HttpPresenter.getInstance().setContext(mContext).setObservable(api.register(body)).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                LiveBean liveBean = new Gson().fromJson(o.toString(), LiveBean.class);
                mView.preparePushSuccess(liveBean);
            }

            @Override
            public void onError(String message) {
                mView.preparePushFailure(message);
            }
        });
    }

    @Override
    public void uploadIcon(File file) {
        RequestBody body=RequestBody.create(MediaType.parse(HttpUtils.MULT_PART_TYPE),file);
        HttpPresenter.getInstance().setContext(mContext).setObservable(api.register(body)).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
