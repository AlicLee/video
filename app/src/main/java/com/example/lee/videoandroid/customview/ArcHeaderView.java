package com.example.lee.videoandroid.customview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lee.videoandroid.R;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Created by zhouwei on 2017/12/5.
 */

public class ArcHeaderView extends View {
    private Paint mPaint;
    private Bitmap mBitmap;
    private int mHeight;
    private int mWidth;
    private RectF mRect = new RectF();
    private Point mCircleCenter;
    private float mRadius;
    private int mStartColor;
    private int mEndColor;
    private LinearGradient mLinearGradient;
    /**
     * 显示图片还是显示色值
     */
    private boolean mIsShowImage = true;
    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    public ArcHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttr(context, attrs);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        //  mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.splash);
        mCircleCenter = new Point();
    }

    private void readAttr(Context context, AttributeSet set) {
        TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.ArcHeaderView);
        mStartColor = typedArray.getColor(R.styleable.ArcHeaderView_startColor, ContextCompat.getColor(context, R.color.lightBlue));
        mEndColor = typedArray.getColor(R.styleable.ArcHeaderView_endColor, ContextCompat.getColor(context, R.color.blue));
        setColor(mStartColor,mEndColor);
        typedArray.recycle();
//        mIsShowImage = typedArray.getBoolean(R.styleable.PerfectArcView_p_arc_showImage, false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        int width = getWidth();
        mWidth = width;
        // 半径
        mRadius = width * 2;
        // 矩形
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = width;
        mRect.bottom = mHeight;
        // 圆心坐标
        mCircleCenter.x = width / 2;
        mCircleCenter.y = mHeight - width * 2;
        mLinearGradient = new LinearGradient(width / 2, 0, width / 2, mHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);
    }

//    /**
//     * 加载网络图片
//     *
//     * @param url
//     */
//    public void setImageUrl(String url) {
//        Picasso.with(getContext()).load(url).into(this);
//    }

    /**
     * @param startColor
     * @param endColor
     */
    public void setColor(@ColorInt int startColor, @ColorInt int endColor) {
        mStartColor = startColor;
        mEndColor = endColor;
        mIsShowImage = false;
        mLinearGradient = new LinearGradient(mWidth / 2, 0, mWidth / 2, mHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(mCircleCenter.x, mCircleCenter.y, mRadius, mPaint);
        mPaint.setXfermode(porterDuffXfermode);
        if (mIsShowImage) {
            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, null, mRect, mPaint);
            }

        } else {
            mPaint.setShader(mLinearGradient);//绘制渐变色
            canvas.drawRect(mRect, mPaint);
        }

        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }


}