package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.LoginContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.login.LoginActivity;
import com.example.lee.videoandroid.view.main.HomeFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginPresenter extends BasePresenter<LoginActivity> implements LoginContact.Presenter {

    @Override
    public void login(UserBean userBean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(userBean));
        HttpPresenter.getInstance().setContext(mView).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                try {
                    UserBean bean = new Gson().fromJson(o.toString(), UserBean.class);
                    mView.loginSuccess(bean);
                } catch (Exception e) {
                    mView.loginFailure(e.toString());
                }
            }

            @Override
            public void onError(String message) {
                mView.loginFailure(message);
            }
        }).create(api.requestLogin(body));
    }
}
