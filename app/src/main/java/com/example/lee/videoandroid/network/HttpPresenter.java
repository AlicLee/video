package com.example.lee.videoandroid.network;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.util.LogUtil;
import com.example.lee.videoandroid.util.SystemUtil;
import com.example.lee.videoandroid.util.ToastUtils;

import java.util.Observer;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpPresenter<T> implements HttpBuilder<T> {
    private HttpTaskListener<T> listener;
    private Context mContex;
    private boolean isAddCommonSubScription = true;//是否加入公共生命周期管理
    private boolean isShowDialog = true;
    private Call<BaseResponse> call;
    private Observable<BaseResponse> observable;

    private static class HttpPresenterBuilder {
        private final static HttpPresenter instance = new HttpPresenter();
    }

    public static HttpPresenter getInstance() {
        return HttpPresenterBuilder.instance;
    }

    private Subscription request(Observable observable) {
        //先检查网络
        if (!SystemUtil.isMobileNetworkConnected(mContex)) {
            ToastUtils.showLongToast(R.string.noNetWorkException);
            if (listener != null)
                listener.onError(mContex.getResources().getString(R.string.noNetWorkException));
            return null;
        }
        if (mContex != null && isShowDialog) {
            HttpUtils.getInstance().showProgressBar(mContex);
        }
        Subscription subscribe = observable
                .subscribeOn(Schedulers.newThread())//请求网络在子线程中
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程中
                .subscribe(new Subscriber<BaseResponse<T>>() {
                    @Override
                    public void onCompleted() {
                        HttpUtils.getInstance().closeProgressBar(mContex);
//                        CommonUtils.getInstance().hideInfoProgressDialog();
                        mContex = null;
                    }

                    @Override
                    public void onError(Throwable e) {
//                        LogUtil.getInstance().e("请求id:" + requstId + "--" + e.getMessage());
//                        CommonUtils.getInstance().hideInfoProgressDialog();
//                        CommonUtils.showToast(context,"网络出了点问题");
                        HttpUtils.getInstance().closeProgressBar(mContex);
                        if (listener != null) {
                            listener.onError(e.getMessage());
                        }
//                        httpPresenter = null;
                        mContex = null;
                    }

                    @Override
                    public void onNext(BaseResponse<T> tBaseResponse) {
                        HttpUtils.getInstance().closeProgressBar(mContex);
//                        CommonUtils.getInstance().hideInfoProgressDialog();
                        if (listener != null) {
                            if (tBaseResponse.code == 200) {//请求成功
                                listener.onSuccess(tBaseResponse.data);
                            } else {
//                                CommonUtils.showToast(MyApplication.getContext(), baseResponseVo.msg);
                                listener.onError(tBaseResponse.message);
                            }
                        }
//                        httpPresenter = null;
                        mContex = null;
                    }
                });
        //activity 或者fragment销毁时 必须销毁所有的请求 不然容易导致空指针
        if (mContex != null && mContex instanceof BaseActivity)
            ((BaseActivity) mContex).addSubscription(subscribe);
        else {
            if (isAddCommonSubScription)
                HttpUtils.getInstance().addSubscription(subscribe);
        }
        return subscribe;
    }

    @Override
    public HttpBuilder setContext(Context context) {
        mContex = context;
        return this;
    }

    @Override
    public HttpBuilder<T> create(Observable observable) {
        if (observable == null) {
            LogUtil.getInstance().e("请设置observable");
            if (listener != null) listener.onError("observable 为空");
            return this;
        }
        request(observable);
        return this;
    }

    @Override
    public HttpBuilder create(Call call) {
        if (!SystemUtil.isMobileNetworkConnected(mContex)) {
            ToastUtils.showLongToast(R.string.noNetWorkException);
            if (listener != null)
                listener.onError(mContex.getResources().getString(R.string.noNetWorkException));
            return null;
        }
        if (mContex != null && isShowDialog) {
            HttpUtils.getInstance().showProgressBar(mContex);
        }
        this.call = call;
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onError(response.message());
                    }
                }
                mContex = null;
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                listener.onError(t.getMessage());
                mContex = null;
            }
        });
        return this;
    }

    @Override
    public HttpBuilder setCallBack(HttpTaskListener<T> listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public HttpBuilder isShowDialog(boolean isShow) {
        this.isShowDialog = isShow;
        return this;
    }

    @Override
    public HttpBuilder isAddCommonSubscription() {
        isAddCommonSubScription = false;
        return this;
    }
}
