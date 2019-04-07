package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.model.LiveBean;

import java.util.List;

public interface HomeContact {
    public interface View {
        public void loadMoreSuccess(List<LiveBean> liveBeans);

        public void loadMoreFailure(String message);

        public void refreshSuccess(List<LiveBean> liveBeans);

        public void refreshFailure(String message);

        public void getLiveByPagesSuccess(List<LiveBean> liveBeans);

        public void getLiveByPagesFailure(String message);
    }

    public interface Presenter {
        public void loadMore(int index, int pageSize);

        public void refresh(int index, int pageSize);

        public void getLiveByPages(int index, int pageSize);
    }
}
