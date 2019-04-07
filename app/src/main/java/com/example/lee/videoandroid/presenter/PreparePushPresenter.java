package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.PreparePushContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.BaseResponse;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.push.PreparePushActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;

public class PreparePushPresenter extends BasePresenter<PreparePushActivity> implements PreparePushContact.Presenter {
    @Override
    public void preparePush(LiveBean bean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(bean));
        HttpPresenter.getInstance().setContext(mContext).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                mView.preparePushSuccess();
            }

            @Override
            public void onError(String message) {
                mView.preparePushFailure(message);
            }
        }).create(api.updateOrInsert(body));
    }

    @Override
    public void uploadIcon(File file) {
        //todo bug
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.MULT_PART_TYPE), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        HttpPresenter.getInstance().setContext(mContext).setCallBack(new HttpTaskListener<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse o) {
                String uploadPicPath=o.data.toString();
                mView.uploadIconSuccess(uploadPicPath);
            }

            @Override
            public void onError(String message) {
                mView.uploadIconFailure(message);
            }
        }).create(api.upload(part));
    }
}
