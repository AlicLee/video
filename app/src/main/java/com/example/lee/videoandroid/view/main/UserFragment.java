package com.example.lee.videoandroid.view.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseFragment;
import com.example.lee.videoandroid.util.RenderScriptGaussianBlur;
import com.example.lee.videoandroid.widget.SettingItemLayout;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserFragment<P> extends BaseFragment {
    @BindView(R.id.user_head_icon)
    ImageView userHeadIcon;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.user_lineView)
    View userLineView;
    @BindView(R.id.user_unLogin)
    TextView userUnLogin;
    @BindView(R.id.user_phone_tv)
    TextView userPhoneTv;
    @BindView(R.id.user_title_layout)
    RelativeLayout userTitleLayout;
    @BindView(R.id.user_collection)
    SettingItemLayout userCollection;
    @BindView(R.id.user_download)
    SettingItemLayout userDownload;
    @BindView(R.id.user_recent)
    SettingItemLayout userRecent;
    @BindView(R.id.user_update)
    SettingItemLayout userUpdate;
    @BindView(R.id.user_about)
    SettingItemLayout userAbout;

    @Override
    public void initData(View view) {
        final RenderScriptGaussianBlur blur = new RenderScriptGaussianBlur(getActivity());
        Observable observable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.outHeight=200;
                options.inPreferredConfig = Bitmap.Config.ALPHA_8;
                BitmapFactory.decodeResource(getResources(),R.drawable.user_head_default,options);
                Bitmap bitmap = blur.blur(10, mBitmap);
                blurRadius++;
                if (blurRadius == 25){
                    blurRadius = 1;
                }
                e.onNext(bitmap);
                e.onComplete();
            }
        }).observeOn(Schedulers.computation()).subscribeOn(AndroidSchedulers.mainThread());
        Observer<Bitmap> observer=new Observer<Bitmap>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Bitmap bitmap) {

            }
        };
        observable.subscribe(observer);
        userTitleLayout.setBackground();
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public int setLayoutView() {
        return R.layout.fragment_user;
    }

    @OnClick({R.id.user_head_icon, R.id.user_title_layout, R.id.user_collection, R.id.user_download, R.id.user_recent, R.id.user_update, R.id.user_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_head_icon:
                break;
            case R.id.user_title_layout:
                break;
            case R.id.user_collection:
                break;
            case R.id.user_download:
                break;
            case R.id.user_recent:
                break;
            case R.id.user_update:
                break;
            case R.id.user_about:
                break;
        }
    }
}
