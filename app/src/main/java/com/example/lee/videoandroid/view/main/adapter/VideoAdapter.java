package com.example.lee.videoandroid.view.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.view.main.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoAdapter extends BaseQuickAdapter<VideoBean, VideoAdapter.VideoViewHolder> {
    Context mContext;
    private  OnItemDownloadListener listener;
    public VideoAdapter(Context context, int layoutResId) {
        super(layoutResId, new ArrayList<VideoBean>());
        mContext = context;
    }

    public VideoAdapter(Context context, int layoutResId, List<VideoBean> videoBeanList) {
        super(layoutResId, videoBeanList);
        mContext = context;
    }

    @Override
    protected void convert(final VideoViewHolder helper, final VideoBean item) {
        helper.titleName.setText(item.getName());
        helper.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemDownload(VideoAdapter.this,item,helper.getLayoutPosition());
            }
        });
        Glide.with(mContext).load(Api.FILE_HOST + item.getCoverPath()).thumbnail(0.8f).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                helper.videoLayout.setBackground(resource);
            }
        });
    }


    @Override
    public void addData(int position, @NonNull VideoBean data) {
        super.addData(position, data);
    }

    @Override
    public void addData(@NonNull VideoBean data) {
        super.addData(data);
    }

    @Override
    public void addData(@NonNull Collection<? extends VideoBean> newData) {
        super.addData(newData);

    }

    public class VideoViewHolder extends BaseViewHolder {
        private RelativeLayout cardLayout, videoLayout, titleLayout;
        private TextView titleName, videoDuration;
        private Button downloadBtn;

        public VideoViewHolder(View itemView) {
            super(itemView);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.card_layout);
            videoLayout = (RelativeLayout) itemView.findViewById(R.id.video_layout);
            titleLayout = (RelativeLayout) itemView.findViewById(R.id.title_layout);
            titleName = (TextView) itemView.findViewById(R.id.title_name);
            videoDuration = (TextView) itemView.findViewById(R.id.video_duration);
            downloadBtn = (Button) itemView.findViewById(R.id.downloadBtn);
        }
    }

    public interface OnItemDownloadListener {
        void onItemDownload(BaseQuickAdapter adapter, VideoBean videoItem, int position);
    }
    public void setOnItemDownloadListener(OnItemDownloadListener itemDownloadListener){
        this.listener=itemDownloadListener;
    }
}
