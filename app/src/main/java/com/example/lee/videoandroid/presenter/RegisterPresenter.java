package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.RegisterContact;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.login.RegisterActivity;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RegisterPresenter extends BasePresenter<RegisterActivity> implements RegisterContact.Presenter {
    @Override
    public void register(UserBean userBean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(userBean));
        HttpPresenter.getInstance().setContext(mContext).setObservable(api.register(body)).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                UserBean registerBean = new Gson().fromJson(o.toString(), UserBean.class);
                mView.registerSuccess(registerBean);
            }

            @Override
            public void onError(String message) {
                mView.registerFailure(message);
            }
        });
    }
}
