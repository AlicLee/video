package com.example.lee.videoandroid.view.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.network.Api;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class HomeAdapter extends BaseQuickAdapter<LiveBean, HomeAdapter.HomeViewHolder> {
    RecyclerView view;
    Context mContext;

    public HomeAdapter(Context context, int layoutId) {
        super(layoutId, new ArrayList<LiveBean>());
        mContext = context;
    }

    public HomeAdapter(Context context, int layoutId, List<LiveBean> beanList) {
        super(layoutId, beanList);
        mContext = context;
    }

    @Override
    protected void convert(final HomeViewHolder helper, LiveBean item) {
//        helper.setText(R.id.title,item.getLiveTitle()).setText(R.id.description,item.getLiveDescription())
//                .setImageResource()
        helper.title.setText(item.getLiveTitle());
        helper.desTv.setText(item.getLiveDescription());
        if (item.getLiveStatus() == 0)
            helper.liveStatus.setText("未开播");
        Glide.with(mContext).load(Api.HOST + item.getLiveIcon()).asBitmap().placeholder(R.drawable.default_live_icon).error(R.drawable.default_live_icon).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                helper.iconLayout.setBackground(new BitmapDrawable(mContext.getResources(), resource));
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                helper.iconLayout.setBackground(errorDrawable);
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                helper.iconLayout.setBackground(placeholder);
            }
        });
    }

    public class HomeViewHolder extends BaseViewHolder {
        RelativeLayout iconLayout;
        TextView title, desTv, preTv, midTv, lastTv, liveStatus;

        public HomeViewHolder(View itemView) {
            super(itemView);
            iconLayout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            title = (TextView) itemView.findViewById(R.id.title);
            desTv = (TextView) itemView.findViewById(R.id.description);
            preTv = (TextView) itemView.findViewById(R.id.pre_tv);
            midTv = (TextView) itemView.findViewById(R.id.mid_tv);
            lastTv = (TextView) itemView.findViewById(R.id.last_tv);
            liveStatus = (TextView) itemView.findViewById(R.id.live_status);
        }
    }
}
