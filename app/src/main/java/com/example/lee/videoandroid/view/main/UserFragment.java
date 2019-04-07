package com.example.lee.videoandroid.view.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseFragment;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.HomeContact;
import com.example.lee.videoandroid.contact.UserContact;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.network.HttpPresenter;
import com.example.lee.videoandroid.presenter.UserPrsenter;
import com.example.lee.videoandroid.util.GlideCircleTransform;
import com.example.lee.videoandroid.util.PermissionUtils;
import com.example.lee.videoandroid.util.PhotoUtil;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.login.LoginActivity;
import com.example.lee.videoandroid.widget.SettingItemLayout;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;


import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class UserFragment extends BaseFragment<UserPrsenter> implements UserContact.View {
    @BindView(R.id.user_head_icon)
    ImageView userHeadIcon;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.user_lineView)
    View userLineView;
    @BindView(R.id.user_unLogin)
    TextView userUnLogin;
    @BindView(R.id.user_phone_tv)
    TextView userPhoneTv;
    @BindView(R.id.user_title_layout)
    RelativeLayout userTitleLayout;
    //    @BindView(R.id.user_collection)
//    SettingItemLayout userCollection;
//    @BindView(R.id.user_download)
//    SettingItemLayout userDownload;
//    @BindView(R.id.user_recent)
//    SettingItemLayout userRecent;
    @BindView(R.id.user_update)
    SettingItemLayout userUpdate;
    @BindView(R.id.user_about)
    SettingItemLayout userAbout;
    Dialog photoDialog;
    View photoDialogView;
    View.OnClickListener photoDialogViewListener;
    private final int USER_LOGIN_REQUESTCODE = 0xc2;//登录
    private final int PHOTO_REQUEST_CAREMA = 0xe1;//UserFragment中onActivityResult拍照
    private final int PHOTO_REQUEST_GALLERY = 0xe2;//UserFragment中onActivityResult从相册中选择
    private final int PHOTO_REQUEST_CUT = 0xe3;//UserFragment中onActivityResult结果
    //    private final int LOGIN = 0xe4;//UserFragment中onActivityResult登录
    UserBean userBean;
    private final String TAG = UserFragment.class.getSimpleName();
    Uri cropUri;//剪切完毕后保存的uri
    Uri takePhotoUri;//拍照完成之后保存的uri
    private final int REQUEST_CAMERA_CODE = 0xff, REQUEST_READ_EXTERNAL_STORAGE = 0xfe;

    @Override
    public void initData(View view) {
        if (getActivity() != null)
            getActivity().getSharedPreferences("name", Context.MODE_PRIVATE);
    }

    @Override
    public void initView(View view) {
        initPhotoDialogView();
        initUserMessage();
    }

    private void initUserMessage() {
        String userBeanString = SharedPreUtil.getString(getActivity(), Settings.SharedPreUserKey, "");
        userBean = new Gson().fromJson(userBeanString, UserBean.class);
        if (userBean == null) {
            showUnLogin();
            return;
        }
        showLogin(userBean);
    }

    /**
     * 显示已登录
     */
    private void showLogin(UserBean userBean) {
        Glide.with(getActivity()).load(Api.FILE_HOST + userBean.getUserIcon()).placeholder(R.drawable.user_head_default).error(R.drawable.user_head_default).transform(new GlideCircleTransform(getActivity())).into(userHeadIcon);
        userNameTv.setText(userBean.getUserName());
        //正则表达式手机号中间加星号
        userPhoneTv.setText(userBean.getUserPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        userNameTv.setVisibility(View.VISIBLE);
        userUnLogin.setVisibility(View.GONE);
        userLineView.setVisibility(View.VISIBLE);
        userPhoneTv.setVisibility(View.VISIBLE);
    }

    /**
     * 显示未登录
     */
    private void showUnLogin() {
        userUnLogin.setText("未登录");
        userUnLogin.setVisibility(View.VISIBLE);
        userLineView.setVisibility(View.GONE);
        userNameTv.setVisibility(View.GONE);
        userPhoneTv.setVisibility(View.GONE);
    }

    private void initPhotoDialogView() {
        photoDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_head_dialog, null);
        photoDialogViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOtherViewClick(v);
            }
        };
    }

    private void showPhotoDialog() {
        if (photoDialog != null) {
            photoDialog.show();
            return;
        }
        photoDialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        photoDialog.setContentView(photoDialogView);
        Window window = photoDialog.getWindow();
        if (photoDialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        photoDialog.show();
        photoDialogView.findViewById(R.id.abroad_takephoto).setOnClickListener(photoDialogViewListener);
        photoDialogView.findViewById(R.id.abroad_choosephoto).setOnClickListener(photoDialogViewListener);
        photoDialogView.findViewById(R.id.abroad_choose_cancel).setOnClickListener(photoDialogViewListener);

    }


    @Override
    public int setLayoutView() {
        return R.layout.fragment_user;
    }

    @OnClick({R.id.user_head_icon, R.id.user_title_layout,
            R.id.user_update, R.id.user_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_head_icon:
                if (userBean != null)
                    showPhotoDialog();
                else
                    ToastUtils.showShortToast("请先登录,点击头像外部可以登录~");
                break;
            case R.id.user_title_layout:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("source", "user");
                startActivityForResult(intent, USER_LOGIN_REQUESTCODE);
//                startActivity(intent);
                break;
//            case R.id.user_collection:
//                break;
//            case R.id.user_download:
//                break;
//            case R.id.user_recent:
//                break;
            case R.id.user_update:
                ToastUtils.showShortToast("已经是最新版了哦~");
                break;
            case R.id.user_about:
                new AlertDialog.Builder(mContext).setTitle("关于软件").setMessage("这个软件是以流媒体为基础做的~").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

    public void onOtherViewClick(View view) {
        switch (view.getId()) {
            case R.id.abroad_takephoto:
//                int selfPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
//                if (selfPermission != PackageManager.PERMISSION_GRANTED) {
//
//                    /**
//                     * 判断该权限请求是否已经被 Denied(拒绝)过。  返回：true 说明被拒绝过 ; false 说明没有拒绝过
//                     *
//                     * 注意：
//                     * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
//                     * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
//                     */
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
//                        Log.i(TAG, "onViewClicked: 该权限请求已经被 Denied(拒绝)过。");
//                        //弹出对话框，告诉用户申请此权限的理由，然后再次请求该权限。
//                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
//                    } else {
//                        Log.i(TAG, "onViewClicked: 该权限请未被denied过");
//                        takePhoto();
//                    }
//                } else {
//                    takePhoto();//打开相机
//                }
                PermissionUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.IPermissionsResult() {
                    @Override
                    public void passPermissons() {
                        takePhoto();
                    }

                    @Override
                    public void forbitPermissons() {

                    }
                });
                break;
            case R.id.abroad_choosephoto:
                //todo start intent to ACTION_PICK
                PermissionUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.IPermissionsResult() {
                    @Override
                    public void passPermissons() {
                        choosePhoto();
                    }

                    @Override
                    public void forbitPermissons() {

                    }
                });
                break;
            case R.id.abroad_choose_cancel:
                photoDialog.dismiss();
                break;
        }
    }

    private void choosePhoto() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("name", Context.MODE_MULTI_PROCESS);
        Matisse.from(this).choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP, MimeType.WEBP))
                .countable(false)
                .maxSelectable(1)
                .theme(R.style.Matisse_Zhihu)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("tag", "调用到了");
        PermissionUtils.getInstance().onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    public void takePhoto() {
        File tmpTakePhotoFile = new File(Environment.getExternalStorageDirectory() + "/" + "tempTakePhoto.jpeg");
        try {
            if (tmpTakePhotoFile.exists()) {
                tmpTakePhotoFile.delete();
            }
            tmpTakePhotoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果android7.0以上的系统，需要做个判断
        if (Build.VERSION.SDK_INT >= 24) {
            takePhotoUri = FileProvider.getUriForFile(getActivity(), "com.example.lee.videoandroid.provider", tmpTakePhotoFile);//7.0
        } else {
            takePhotoUri = Uri.fromFile(tmpTakePhotoFile); //7.0以下
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //相册
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    List<Uri> dataList = Matisse.obtainResult(data);
                    if (dataList != null && dataList.size() == 1) {
                        cropPhoto(dataList.get(0));
                    }
                }
                photoDialog.dismiss();
                break;
            //照相
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_CANCELED) {
                    photoDialog.dismiss();
                    break;
                }
                //启动图像裁剪
                cropPhoto(takePhotoUri);
                photoDialog.dismiss();
                break;
            //剪裁
            case PHOTO_REQUEST_CUT:
                if (resultCode == RESULT_OK) {
//                    SharedPreferences sharedPreferences=getActivity().getSharedPreferences("flags", Context.MODE_WORLD_READABLE);
                    final String cropFilePath = PhotoUtil.getPath(getActivity(), cropUri);
                    if (cropFilePath != null) {
                        PermissionUtils.getInstance().checkPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.IPermissionsResult() {
                            @Override
                            public void passPermissons() {
                                File file = new File(cropFilePath);
                                mPresenter.onUploadHeadFile(file);
                            }

                            @Override
                            public void forbitPermissons() {

                            }
                        });
//                        int selfPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
//                        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
//
//                            /**
//                             * 判断该权限请求是否已经被 Denied(拒绝)过。  返回：true 说明被拒绝过 ; false 说明没有拒绝过
//                             *
//                             * 注意：
//                             * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
//                             * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
//                             */
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                                Log.i(TAG, "onViewClicked: 该权限请求已经被 Denied(拒绝)过。");
//                                //弹出对话框，告诉用户申请此权限的理由，然后再次请求该权限。
//                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
//                            } else {
//                                Log.i(TAG, "onViewClicked: 该权限请未被denied过");
//                                takePhoto();
//                            }
//                        } else {
//                            takePhoto();//打开相机
//                        }
                    } else
                        ToastUtils.showShortToast("剪切失败,没有得到文件");
                }
                photoDialog.dismiss();
                break;
            case USER_LOGIN_REQUESTCODE:
                if (resultCode != RESULT_OK) {
                    break;
                }
                String userLoginData = data.getStringExtra("userLoginData");
                UserBean userBean = new Gson().fromJson(userLoginData, UserBean.class);
                showLogin(userBean);
                break;
        }

    }

    /**
     * 启动剪裁,以及剪裁的一些属性的设置
     *
     * @param uri 要剪裁的图片的uri
     */
    private void cropPhoto(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        File tmpCropFile = new File(Environment.getExternalStorageDirectory() + "/" + "tempCrop.jpeg");
        try {
            if (tmpCropFile.exists()) {
                tmpCropFile.delete();
            }
            tmpCropFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (Build.VERSION.SDK_INT >= 24) {
//            cropUri = FileProvider.getUriForFile(this.getActivity(), "com.example.lee.videoandroid.provider", tmpCropFile);
//        } else {
        cropUri = Uri.fromFile(tmpCropFile);
//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    @Override
    public void onUploadHeadFileSuccess(String path) {
        userBean.setUserIcon(path);
        mPresenter.onUpdateUserMessage(userBean);
    }

    @Override
    public void onUploadHeadFileFailure(String message) {
        Log.e(TAG, "错误:" + message);
        ToastUtils.showShortToast("更新头像失败,错误原因:" + message);
    }

    @Override
    public void onUpdateUserMessageSuccess(UserBean user) {
        Glide.with(getActivity()).load(Api.FILE_HOST + user.getUserIcon())
                .placeholder(R.drawable.user_head_default).transform(new GlideCircleTransform(getActivity())).
                into(userHeadIcon);
//        mPresenter.onUpdateUserMessage();
    }

    @Override
    public void onUpdateUserMessageFailure(String message) {
        Log.e(TAG, "错误:" + message);
        ToastUtils.showShortToast("更新头像失败,错误原因:" + message);
    }

}
