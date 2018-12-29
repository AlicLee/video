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
    private HomeFragment fragment;

    @Override
    public void setView(HomeFragment v) {
        super.setView(v);
        this.fragment = v;
    }

    @Override
    public void getLiveByPages(int index, int pageSize) {
        HttpPresenter.getInstance().setContext(fragment.getActivity()).setObservable(api.requestLiveByPages(index, pageSize)).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                try {
                    List<LiveBean> liveBeans = new Gson().fromJson(o.toString(), new TypeToken<List<LiveBean>>() {
                    }.getType());
                    fragment.getLiveSuccess(liveBeans);
                } catch (Exception e) {
                    fragment.getLiveFailure(e.toString());
                }
            }

            @Override
            public void onError(String message) {
                fragment.getLiveFailure(message);
            }
        }).create();

    }
}
