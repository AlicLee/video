package com.example.lee.videoandroid.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.videoandroid.R;

public final class SettingItemLayout extends RelativeLayout {
    private Context mContext;
    private AttributeSet mAttrs;
    private int mDefStyleAttr;

    public SettingItemLayout(Context context) {
        super(context);
        mContext = context;
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
        initView();
    }

    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAttrs = attrs;
        mDefStyleAttr = defStyleAttr;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mAttrs = attrs;
        mDefStyleAttr = defStyleAttr;
        initView();
    }

    public void initView() {
        TypedArray ta = mContext.obtainStyledAttributes(mAttrs, R.styleable.settingItem);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.view_settingitem, this, true);
        final ImageView icon = (ImageView) view.findViewById(R.id.setting_item_icon);
        final TextView name = (TextView) view.findViewById(R.id.setting_item_name);
        final View lineView = (View) view.findViewById(R.id.setting_item_line);
        Drawable drawable = ta.getDrawable(R.styleable.settingItem_iconBg);
        icon.setImageDrawable(drawable);
        name.setText(ta.getText(R.styleable.settingItem_textName));
        if (ta.getBoolean(R.styleable.settingItem_haveLine, false))
            lineView.setVisibility(View.VISIBLE);
        else
            lineView.setVisibility(View.INVISIBLE);
        ta.recycle();
    }
}
