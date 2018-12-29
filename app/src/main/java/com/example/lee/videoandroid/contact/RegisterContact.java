package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.UserBean;

public interface RegisterContact {
    public interface View {
        public void registerSuccess(UserBean userBean);

        public void registerFailure(String errorMessage);
    }

    public interface Presenter {
        public void register(UserBean userBean);
    }
}
