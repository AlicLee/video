package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.UserContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.BaseResponse;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.main.UserFragment;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UserPrsenter extends BasePresenter<UserFragment> implements UserContact.Presenter {

    @Override
    public void onUploadHeadFile(final File file) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.MULT_PART_TYPE), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
//        Observable observable = ;
        HttpPresenter.getInstance().setContext(mContext).setCallBack(new HttpTaskListener<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                String uploadPicPath = baseResponse.data.toString();
                mView.onUploadHeadFileSuccess(uploadPicPath);
//                mView.uploadIconSuccess(uploadPicPath);
            }

            @Override
            public void onError(String message) {
                mView.onUploadHeadFileFailure(message);
            }
        }).create(api.upload(part));
    }

    @Override
    public void onUpdateUserMessage(UserBean bean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(bean));
        Observable observable = api.updateUser(body);
        HttpPresenter.getInstance().setContext(mContext).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                UserBean userBean = new Gson().fromJson(o.toString(), UserBean.class);
                mView.onUpdateUserMessageSuccess(userBean);
            }

            @Override
            public void onError(String message) {
                mView.onUpdateUserMessageFailure(message);
            }
        }).create(observable);
    }
}
