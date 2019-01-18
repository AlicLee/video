package com.example.lee.videoandroid.view.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.model.LiveBean;

import java.util.List;

import okhttp3.OkHttpClient;

public class HomeAdapter extends BaseQuickAdapter<LiveBean, HomeAdapter.HomeViewHolder> {
    RecyclerView view;
    Context mContext;

    public HomeAdapter(Context context, int layoutId) {
        super(layoutId);
        mContext = context;
    }

    @Override
    protected void convert(HomeViewHolder helper, LiveBean item) {
//        helper.
    }

    public class HomeViewHolder extends BaseViewHolder {
        RelativeLayout iconLayout;
        TextView title;
        TextView desTv;
        TextView preTv;
        TextView midTv;
        TextView lastTv;

        public HomeViewHolder(View itemView) {
            super(itemView);
            iconLayout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            title = (TextView) itemView.findViewById(R.id.title);
            desTv = (TextView) itemView.findViewById(R.id.description);
            preTv = (TextView) itemView.findViewById(R.id.pre_tv);
            midTv = (TextView) itemView.findViewById(R.id.mid_tv);
            lastTv = (TextView) itemView.findViewById(R.id.last_tv);
        }
    }
}
