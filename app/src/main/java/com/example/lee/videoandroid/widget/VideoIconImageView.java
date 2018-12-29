//package com.example.lee.videoandroid.widget;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.media.Image;
//import android.os.Build;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.ImageView;
//
//public class VideoIconImageView extends ImageView {
//    private View preView, midView, lastView;
//    private int width, height;
//    private int left, top, right, bottom;
//
//    public VideoIconImageView(Context context) {
//        super(context);
//    }
//
//    public VideoIconImageView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public VideoIconImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public VideoIconImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        this.left = left;
//        this.top = top;
//        this.right = right;
//        this.bottom = bottom;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        this.width = widthMeasureSpec;
//        this.height = heightMeasureSpec;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//    }
//
//    public void setMidView(View view) {
//        this.midView = view;
//        midView.measure(this.width / 3, view.getHeight());
//        midView.layout();
//    }
//
//    public void setPreView(View view) {
//        this.preView = view;
//    }
//
//    public void setLastView(View view) {
//        this.lastView = view;
//    }
//
//    public void refreshView() {
//        invalidate();
//    }
//}
