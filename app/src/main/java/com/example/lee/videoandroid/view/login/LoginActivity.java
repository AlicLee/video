package com.example.lee.videoandroid.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.LoginContact;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.network.HttpTaskListener;
import com.example.lee.videoandroid.network.HttpUtils;
import com.example.lee.videoandroid.presenter.LoginPresenter;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.TUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.main.MainActivity;
import com.example.lee.videoandroid.view.push.PreparePushActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContact.View {

    public final int REQUEST_LOGIN = 1;

    @BindView(R.id.nickName_ev)
    EditText nickNameEv;
    @BindView(R.id.nickName_inputLayout)
    TextInputLayout nickNameInputLayout;
    @BindView(R.id.password_ev)
    EditText passwordEv;
    @BindView(R.id.password_inputLayout)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.register_btn)
    Button registerBtn;

    @Override
    public int setLayoutView() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        configActionBar();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nickNameEv.getText().toString();
                String userPassword = passwordEv.getText().toString();
                if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(userPassword)) {
                    ToastUtils.showLongToast("用户名或者密码为空");
                    return;
                }
                UserBean userBean = new UserBean();
                userBean.setUserPhone(userName);
                userBean.setUserPassword(userPassword);
                mPresenter.login(userBean);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setTitle(R.string.login_tv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void loginSuccess(UserBean userBean) {
        SharedPreUtil.saveString(this, Settings.SharedPreUserKey, new Gson().toJson(userBean));
        Intent intent = new Intent(LoginActivity.this, PreparePushActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginFailure(String errorMessage) {
        Log.e("LoginActivity", "loginFailure: " + errorMessage);
        ToastUtils.showShortToast(errorMessage);
    }
}
