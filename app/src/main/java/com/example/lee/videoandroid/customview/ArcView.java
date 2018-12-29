package com.example.lee.videoandroid.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.lee.videoandroid.R;

public class ArcView extends View {
    private int mWidth;
    private int mHeight;
    private int mArcHeight;
    private int mBgColor;
    private Paint mPaint;
    private Context mContext;
    Rect rect;
    Path path;

    public ArcView(Context context) {
        super(context);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context, attrs);
    }

//    private void init(Context context, AttributeSet attrs) {
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView);
//        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0);
//        mBgColor = typedArray.getColor(R.styleable.ArcView_bgColor, getResources().getColor(R.color.blue));
//        mContext = context;
//        mPaint = new Paint();
//        rect = new Rect(0, 0, mWidth, mHeight - mArcHeight);
//        path = new Path();
//        typedArray.recycle();
//    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);
        canvas.drawRect(rect, mPaint);
        path.moveTo(0, mHeight - mArcHeight);
        path.quadTo(mWidth / 2, mHeight, mWidth, mHeight - mArcHeight);
        canvas.drawPath(path, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

}
