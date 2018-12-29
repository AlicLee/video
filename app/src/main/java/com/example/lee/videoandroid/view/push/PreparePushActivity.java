package com.example.lee.videoandroid.view.push;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.PreparePushContact;
import com.example.lee.videoandroid.customview.ArcHeaderView;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.presenter.PreparePushPresenter;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreparePushActivity extends BaseActivity<PreparePushPresenter> implements PreparePushContact.View {
    @BindView(R.id.title_ev)
    EditText titleEv;
    @BindView(R.id.description_tv)
    TextView descriptionTv;
    @BindView(R.id.arcView)
    ArcHeaderView arcView;
    @BindView(R.id.add_icon_btn)
    ImageView addIconBtn;
    private final int REQUEST_CODE_CHOOSE = 388;
    @BindView(R.id.startPreparePush)
    Button startPreparePush;
    private List<String> picPathList;
    private AlertDialog descriptionDialog;
    private LiveBean bean = new LiveBean();

    @Override
    public int setLayoutView() {
        return R.layout.activity_preparepush;
    }

    @Override
    public void initView() {
        configActionBar();
        createDescriptionDialog();

    }

    private void createDescriptionDialog() {
        final View view = LayoutInflater.from(PreparePushActivity.this).inflate(R.layout.item_dialog, null, false);
        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(R.id.dialog_evLayout);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_ev);
        descriptionDialog = new AlertDialog.Builder(PreparePushActivity.this).setTitle("设置公告").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!StringUtil.isEmpty(editText.getText().toString())) {
                    descriptionTv.setText(editText.getText().toString());
                }
                dialog.dismiss();
            }
        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(view).create();
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setTitle("开启直播");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            picPathList = Matisse.obtainPathResult(data);
            if (picPathList.size() == 1) {
                mPresenter.uploadIcon(new File(picPathList.get(0)));
            }
        }
    }


    @OnClick({R.id.description_tv, R.id.add_icon_btn, R.id.startPreparePush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.description_tv:
                descriptionDialog.show();
                break;
            case R.id.add_icon_btn:
                Matisse.from(PreparePushActivity.this)
                        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP, MimeType.WEBP))
                        .maxSelectable(1)
                        .theme(R.style.Matisse_Zhihu)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.startPreparePush:
                if (StringUtil.isEmpty(titleEv.getText().toString())) {
                    ToastUtils.showShortToast("标题不能为空");
                    return;
                }
                if (StringUtil.isEmpty(bean.getLiveIcon())) {
                    ToastUtils.showShortToast("请选择一个封面");
                    return;
                }
                String userBeanString = SharedPreUtil.getString(this, Settings.SharedPreUserKey, "");
                if (userBeanString.length() == 0) {
                    ToastUtils.showShortToast("用户未登陆");
                    return;
                }
                UserBean userBean = new Gson().fromJson(userBeanString, UserBean.class);
                bean.setUserId(userBean.getId());
                bean.setLiveStatus(0);
                if (mPresenter != null)
                    mPresenter.preparePush(bean);
                break;
        }
    }

    @Override
    public void preparePushSuccess(LiveBean liveBean) {
//        Intent intent=new Intent(PreparePushActivity.this,);
    }

    @Override
    public void preparePushFailure(String errorMessage) {

    }

    @Override
    public void uploadIconSuccess(String path) {
        Glide.with(this).load(picPathList.get(0)).into(addIconBtn);
        bean.setLiveIcon(path);
    }

    @Override
    public void uploadIconFailure(String errorMessage) {
        ToastUtils.showShortToast("上传成功");
    }

}
