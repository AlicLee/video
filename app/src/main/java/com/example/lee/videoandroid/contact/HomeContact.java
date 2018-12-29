package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.model.LiveBean;

import java.util.List;

public interface HomeContact {
    public interface View {
        public void getLiveSuccess(List<LiveBean> liveBeans);

        public void getLiveFailure(String message);
    }

    public interface Presenter {
        public void getLiveByPages(int index,int pageSize);
    }
}
