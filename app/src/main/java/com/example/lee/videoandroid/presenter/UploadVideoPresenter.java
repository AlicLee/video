package com.example.lee.videoandroid.presenter;

import com.example.lee.videoandroid.base.BasePresenter;
import com.example.lee.videoandroid.contact.UploadVideoContact;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.BaseResponse;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.view.video.ProgressListener;
import com.example.lee.videoandroid.view.video.ProgressRequestBody;
import com.example.lee.videoandroid.view.video.UploadVideoActivity;
import com.google.gson.Gson;

import java.io.File;
import java.util.Observable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UploadVideoPresenter extends BasePresenter<UploadVideoActivity> implements UploadVideoContact.Presenter {
    Call<BaseResponse> videoCall;

    @Override
    public void uploadVideo(File file) {
        RequestBody fileBody = new ProgressRequestBody(RequestBody.create(MediaType.parse(HttpUtils.MULT_PART_TYPE), file), new ProgressListener() {
            @Override
            public void progress(final int progress) {
                mView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.uploadVideoProgress(progress);
                    }
                });
            }
        });
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        videoCall = api.uploadVideo(part);
        HttpPresenter.getInstance().setContext(mContext).create(videoCall).setCallBack(new HttpTaskListener<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                String uploadPicPath = baseResponse.data.toString();
                mView.uploadVideoSuccess(uploadPicPath);
            }

            @Override
            public void onError(String message) {
                mView.uploadVideoFailure(message);
            }
        });
    }

    @Override
    public void uploadVideoCancel() {
        videoCall.cancel();
        if (videoCall.isCanceled()) {
            mView.uploadVideoCancelSuccess();
        } else {
            mView.uploadVideoCancelFailure();
        }
    }

    @Override
    public void uploadCover(File file) {
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName());
        HttpPresenter.getInstance().setContext(mContext).create(api.upload(part)).setCallBack(new HttpTaskListener<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                String uploadPicPath = baseResponse.data.toString();
                mView.uploadCoverSuccess(uploadPicPath);
            }

            @Override
            public void onError(String message) {
                mView.uploadCoverFailure(message);
            }
        });
    }

    @Override
    public void insertVideo(VideoBean videoBean) {
        RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), new Gson().toJson(videoBean));
        HttpPresenter.getInstance().setContext(mContext).create(api.insertVideo(body)).setCallBack(new HttpTaskListener() {
            @Override
            public void onSuccess(Object o) {
                VideoBean bean = new Gson().fromJson(o.toString(), VideoBean.class);
                mView.insertVideoSuccess(bean);
            }

            @Override
            public void onError(String message) {
                mView.insertVideoFailure(message);
            }
        });
    }
}
