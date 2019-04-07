package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.VideoBean;

import java.util.List;

public interface VideoContact {
    public interface View {
        public void getVideoByPagesSuccess(List<VideoBean> videoBeanList);

        public void getVideoByPagesFailure(String message);

        public void onRefreshSuccess(List<VideoBean> videoBeanList);

        public void onRefreshFailure(String message);

        public void onLoadMoreSuccess(List<VideoBean> videoBeanList);

        public void onLoadMoreFailure(String message);
    }

    public interface Presenter {
        public void getVideoByPages(int pageIndex, int pageSize);

        public void refresh(int pageIndex, int pageSize);

        public void loadMore(int pageIndex, int pageSize);

    }
}
