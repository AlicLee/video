package com.example.lee.videoandroid.contact;

import com.example.lee.videoandroid.model.UserBean;

public interface LoginContact {
    public interface View{
        public void loginSuccess(UserBean userBean);
        public void loginFailure(String errorMessage);
    }
    public interface Presenter{
        public void login(UserBean userBean);
    }
}
