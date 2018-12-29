package com.example.lee.videoandroid.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.view.push.PreparePushActivity;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

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
                Intent intent = new Intent(LoginActivity.this, PreparePushActivity.class);
                startActivity(intent);
//                String userName = nickNameEv.getText().toString();
//                String userPassword = passwordEv.getText().toString();
//                if (!StringUtil.isEmpty(userName) || !StringUtil.isEmpty(userPassword)) {
//                    ToastUtils.showLongToast("用户名或者密码为空");
//                    return;
//                }
//                JSONObject object = new JSONObject();
//                try {
//                    object.put("userName", nickNameEv.getText().toString());
//                    object.put("userPassword", passwordEv.getText().toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                RequestBody body = RequestBody.create(MediaType.parse(HttpUtils.JSON_CONTENT_TYPE), object.toString());
//                HttpPresenter.getInstance().
//                        setContext(LoginActivity.this)
//                        .setObservable(api.requestLogin(body)).setCallBack(new HttpTaskListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//
//                    }
//
//                    @Override
//                    public void onError(String message) {
//
//                    }
//                }).create();
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


}
