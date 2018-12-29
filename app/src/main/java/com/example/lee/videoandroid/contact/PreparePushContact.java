package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.LiveBean;

import java.io.File;

public interface PreparePushContact {
    public interface View {
        public void preparePushSuccess(LiveBean liveBean);

        public void preparePushFailure(String errorMessage);

        void uploadIconSuccess(String path);

        void uploadIconFailure(String errorMessage);
    }

    public interface Presenter {
        void preparePush(LiveBean bean);

        void uploadIcon(File file);
    }
}
