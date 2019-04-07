package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;

import java.io.File;

import rx.Subscription;

public interface UserContact {
    public interface View {
        public void onUploadHeadFileSuccess(String path);

        public void onUploadHeadFileFailure(String message);

        public void onUpdateUserMessageSuccess(UserBean user);

        public void onUpdateUserMessageFailure(String message);
    }

    public interface Presenter {
        public void onUploadHeadFile(File file);

        public void onUpdateUserMessage(UserBean bean);
    }
}
