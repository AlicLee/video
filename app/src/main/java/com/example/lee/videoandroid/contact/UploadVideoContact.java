package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.VideoBean;

import java.io.File;

public interface UploadVideoContact {
    public interface View {
        public void uploadVideoSuccess(String path);

        public void uploadVideoFailure(String message);

        public void uploadVideoProgress(int progress);

        public void uploadVideoCancelSuccess();

        public void uploadVideoCancelFailure();

        public void uploadCoverSuccess(String path);

        public void uploadCoverFailure(String message);

        public void insertVideoSuccess(VideoBean videoBean);

        public void insertVideoFailure(String message);
    }

    public interface Presenter {
        public void uploadVideo(File file);

        public void uploadVideoCancel();

        public void uploadCover(File file);

        public void insertVideo(VideoBean videoBean);

    }
}
