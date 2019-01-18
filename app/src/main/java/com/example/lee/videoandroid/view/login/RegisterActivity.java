package com.example.lee.videoandroid.view.login;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.RegisterContact;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.presenter.RegisterPresenter;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.google.gson.Gson;

import android.support.v7.app.ActionBar;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity implements RegisterContact.View {

    @BindView(R.id.nickName_ev)
    EditText nickNameEv;
    @BindView(R.id.nickName_inputLayout)
    TextInputLayout nickNameInputLayout;
    @BindView(R.id.password_ev)
    EditText passwordEv;
    @BindView(R.id.password_inputLayout)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.userName_ev)
    EditText userNameEv;
    @BindView(R.id.userName_inputLayout)
    TextInputLayout userNameInputLayout;
    @BindView(R.id.email_ev)
    EditText emailEv;
    @BindView(R.id.email_inputLayout)
    TextInputLayout emailInputLayout;
    @BindView(R.id.sex_group)
    RadioGroup sexGroup;
    @BindView(R.id.register_btn)
    Button registerBtn;
    boolean sex = true;//true等于男 false 等于女

    @Override
    public int setLayoutView() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        configActionBar();
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.man_btn) {
                    sex = true;
                } else {
                    sex = false;
                }
            }
        });
        register();
    }

    private void register() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(nickNameEv.getText().toString())) {
                    nickNameInputLayout.setError("手机号不能为空");
                    nickNameInputLayout.setErrorTextAppearance(R.style.error_appearance);
                } else if (StringUtil.isEmpty(passwordEv.getText().toString())) {
                    passwordInputLayout.setError("密码不能为空");
                    passwordInputLayout.setErrorTextAppearance(R.style.error_appearance);
                } else if (StringUtil.isEmpty(userNameEv.getText().toString())) {
                    userNameInputLayout.setError("用户名不能为空");
                    userNameInputLayout.setErrorTextAppearance(R.style.error_appearance);
                } else {
                    UserBean userBean = new UserBean();
                    userBean.setSex(sex);
                    userBean.setUserPhone(nickNameEv.getText().toString());
                    userBean.setUserPassword(passwordEv.getText().toString());
                    userBean.setUserName(userNameEv.getText().toString());
                    if (mPresenter instanceof RegisterPresenter) {
                        RegisterPresenter presenter = (RegisterPresenter) mPresenter;
                        presenter.register(userBean);
                    }
                }
            }
        });
    }

    public void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.register_tv);
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
    public void registerSuccess(UserBean userBean) {
        SharedPreUtil.saveString(this, Settings.SharedPreUserKey, new Gson().toJson(userBean));
        Intent intent=new Intent();
    }

    @Override
    public void registerFailure(String errorMessage) {
        ToastUtils.showShortToast("注册失败");
    }
}
