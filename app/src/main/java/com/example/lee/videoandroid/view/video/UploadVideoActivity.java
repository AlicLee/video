package com.example.lee.videoandroid.view.video;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.UploadVideoContact;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.presenter.UploadVideoPresenter;
import com.example.lee.videoandroid.util.PermissionUtils;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.util.UriToPathUtil;
import com.example.lee.videoandroid.view.login.LoginActivity;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadVideoActivity extends BaseActivity<UploadVideoPresenter> implements UploadVideoContact.View {
    @BindView(R.id.videoImg)
    ImageView videoImg;
    @BindView(R.id.selectVideo)
    Button selectVideo;
    @BindView(R.id.uploadVideo)
    Button uploadVideo;
    Uri videoUri;
    private final int SELECT_VIDEO = 0xF1;
    private final int VIDEO_LOGIN_REQUESTCODE = 0xf2;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.linearProgressView)
    ProgressBar linearProgressView;

    private boolean isUpload = false;
    private File videoFile;
    private UserBean userBean;
    private String videoFilePath;
    private String coverPath;
    private boolean isExitCall = false;
    private boolean isUploadSuccess = false;

    @Override
    public int setLayoutView() {
        return R.layout.activity_videoupload;
    }

    @Override
    public void initView() {
        linearProgressView.setMax(1000);
        configActionBar();
    }

    @Override
    public void initData() {
        String userBeanData = SharedPreUtil.getString(this, Settings.SharedPreUserKey, "");
        if (!StringUtil.isEmpty(userBeanData)) {
            userBean = new Gson().fromJson(userBeanData.toString(), UserBean.class);
        }
        getPremission();
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setTitle("上传视频");
    }

    private void getPremission() {
        PermissionUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {

            }

            @Override
            public void forbitPermissons() {
                ToastUtils.showShortToast("您必须允许读取存储卡权限才可以继续");
                getPremission();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(UploadVideoActivity.this, requestCode, permissions, grantResults);
    }

    @OnClick({R.id.selectVideo, R.id.uploadVideo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selectVideo:
                videoUri = null;
//                uploadVideo.setEnabled(false);
                if (isUpload) {
                    new AlertDialog.Builder(this).setMessage("正在上传中,是否要取消上传").setTitle("取消上传")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPresenter.uploadVideoCancel();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                } else {
                    selectVideo();
                }
                break;
            case R.id.uploadVideo:
                String videoName = editText.getText().toString();
                if (!isUploadSuccess) {
                    ToastUtils.showShortToast("您必须选择一个要上传的视频");
                    break;
                }
                if (StringUtil.isEmpty(videoName)) {
                    ToastUtils.showShortToast("视频名字不能为空");
                    break;
                }
                if (userBean == null || userBean.getId() == 0) {
                    Intent intent = new Intent(UploadVideoActivity.this, LoginActivity.class);
                    intent.putExtra("source", "user");
                    startActivityForResult(intent, VIDEO_LOGIN_REQUESTCODE);
                    break;
                }
                VideoBean videoBean = new VideoBean();
                videoBean.setName(videoName);
                videoBean.setPath(videoFilePath);
                videoBean.setUserid(userBean.getId());
                mPresenter.insertVideo(videoBean);
                break;
        }
    }

    /**
     * 选择视频
     */
    public void selectVideo() {
        isExitCall = false;
        Matisse.from(UploadVideoActivity.this)
                .choose(MimeType.ofVideo())
                .maxSelectable(1)
                .theme(R.style.Matisse_Zhihu)
                .forResult(SELECT_VIDEO);
    }


    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (isUpload) {
            new AlertDialog.Builder(this).setMessage("正在上传中,是否要取消上传").setTitle("取消上传")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isExitCall = true;
                            mPresenter.uploadVideoCancel();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_VIDEO:
                if (resultCode != RESULT_OK) {
                    break;
                }
                List<Uri> dataList = Matisse.obtainResult(data);
                if (dataList != null && dataList.size() == 1) {
                    videoUri = dataList.get(0);
                    isUpload = true;
                    selectVideo.setText("重新选择");
                    File selectFile = new File(UriToPathUtil.getRealFilePath(this, videoUri));
                    editText.setText(selectFile.getName());
                    Log.e("lsp", "onActivityResult: path" + selectFile.getPath());
                    mPresenter.uploadVideo(selectFile);
                    linearProgressView.setProgress(0);
                    isUploadSuccess = false;
                    Glide.with(this).load(selectFile).into(videoImg);
                } else {
                    ToastUtils.showShortToast("啊偶,出了一点问题,请重试");
                }
                break;
            case VIDEO_LOGIN_REQUESTCODE:
                if (resultCode != RESULT_OK) {
                    break;
                }
                String userLoginData = data.getStringExtra("userLoginData");
                userBean = new Gson().fromJson(userLoginData, UserBean.class);
                break;
//            case REQUEST_CODE_CHOOSE:
//                if (resultCode == RESULT_CANCELED) {
//                    break;
//                }
//                List<Uri> dataList = Matisse.obtainResult(data);
//                if (dataList != null && dataList.size() == 1) {
//                    cropPhoto(dataList.get(0));
//                }
//                break;
//            case REQUEST_PHOTO_CUT:
//                if (resultCode == RESULT_OK) {
//                    String cropFilePath = PhotoUtil.getPath(this, cropUri);
//                    if (cropFilePath != null)
//                        mPresenter.uploadIcon(new File(cropFilePath));
//                    else
//                        ToastUtils.showShortToast("剪切失败,没有得到文件");
//                }
//                break;
        }
    }

    @Override
    public void uploadVideoSuccess(String path) {
        videoFilePath = path;
        isUploadSuccess = true;
    }

    @Override
    public void uploadVideoFailure(String message) {
        ToastUtils.showShortToast(message);
//        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadVideoProgress(int progress) {
        Log.e("lsp", "uploadVideoProgress: " + progress);
        linearProgressView.setProgress(progress);
//        ToastUtils.showShortToast("上传进度:" + progress);
    }

    @Override
    public void uploadVideoCancelSuccess() {
        ToastUtils.showShortToast("取消成功");
        if (!isExitCall) {
            selectVideo();
        } else {
            finish();
        }
    }

    @Override
    public void uploadVideoCancelFailure() {
        ToastUtils.showShortToast("取消失败,未知原因");
    }

    @Override
    public void uploadCoverSuccess(String path) {
        Glide.with(this).load(Api.FILE_HOST + path).thumbnail(0.8f).into(videoImg);
        coverPath = path;
    }

    @Override
    public void uploadCoverFailure(String message) {
        ToastUtils.showShortToast(message);
    }

    @Override
    public void insertVideoSuccess(VideoBean videoBean) {
        ToastUtils.showShortToast("上传成功视频~");
        this.finish();
    }

    @Override
    public void insertVideoFailure(String message) {
        ToastUtils.showShortToast("上传失败:" + message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
