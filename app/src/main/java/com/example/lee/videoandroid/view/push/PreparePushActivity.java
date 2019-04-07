package com.example.lee.videoandroid.view.push;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.presenter.PreparePushPresenter;
import com.example.lee.videoandroid.util.PermissionUtils;
import com.example.lee.videoandroid.util.PhotoUtil;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.main.MainActivity;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
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
    private View dialogView;
    private TextInputLayout dialogInputLayout;
    private EditText dialogEditText;
    private String dialogEditValue;
    private final int CODE_FOR_WRITE_PERMISSION = 0xff;
    private final int REQUEST_PHOTO_CUT = 0xfe;
    File tmpCropFile = new File(Environment.getExternalStorageDirectory() + "/" + "temp.jpeg");
    Uri cropUri;

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
        dialogView = LayoutInflater.from(PreparePushActivity.this).inflate(R.layout.item_dialog, null, false);
        dialogInputLayout = (TextInputLayout) dialogView.findViewById(R.id.dialog_evLayout);
        dialogEditText = (EditText) dialogView.findViewById(R.id.dialog_ev);
        descriptionDialog = new AlertDialog.Builder(PreparePushActivity.this).setTitle("设置公告").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(dialogView).create();
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
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_CANCELED) {
                    break;
                }
                List<Uri> dataList = Matisse.obtainResult(data);
                if (dataList != null && dataList.size() == 1) {
                    cropPhoto(dataList.get(0));
                }
                break;
            case REQUEST_PHOTO_CUT:
                if (resultCode == RESULT_OK) {
                    String cropFilePath = PhotoUtil.getPath(this, cropUri);
                    if (cropFilePath != null)
                        mPresenter.uploadIcon(new File(cropFilePath));
                    else
                        ToastUtils.showShortToast("剪切失败,没有得到文件");
                }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        // 裁剪框的比例，16：9
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 9);
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 250);
//        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        try {
            if (tmpCropFile.exists()) {
                tmpCropFile.delete();
            }
            tmpCropFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (Build.VERSION.SDK_INT >= 24) {
//            cropUri = FileProvider.getUriForFile(this, "com.example.lee.videoandroid.provider", tmpCropFile);
//        } else {
        cropUri = Uri.fromFile(tmpCropFile);
//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, REQUEST_PHOTO_CUT);
    }


    @OnClick({R.id.description_tv, R.id.add_icon_btn, R.id.startPreparePush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.description_tv:
                descriptionDialog.show();
                setDialogPositiveClick();
                break;
            case R.id.add_icon_btn:
                getPermission();
//                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    //用户默认不同意权限
//                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                CODE_FOR_WRITE_PERMISSION);
//                        return;
//                        //用户默认同意权限
//                    } else {
//                        requestToOpenGALLERY();
//                    }
//                } else {
//                    requestToOpenGALLERY();
//                }
                break;
            case R.id.startPreparePush:
                if (StringUtil.isEmpty(titleEv.getText().toString())) {
                    ToastUtils.showShortToast("标题不能为空");
                    break;
                }
                if (StringUtil.isEmpty(bean.getLiveIcon())) {
                    ToastUtils.showShortToast("请选择一个封面");
                    break;
                }
                String userBeanString = SharedPreUtil.getString(this, Settings.SharedPreUserKey, "");
                if (StringUtil.isEmpty(userBeanString)) {
                    ToastUtils.showShortToast("用户未登陆");
                    break;
                }
                UserBean userBean = new Gson().fromJson(userBeanString, UserBean.class);
                bean.setUserId(userBean.getId());
                bean.setLiveTitle(titleEv.getText().toString());
                bean.setLiveDescription(dialogEditValue);
                bean.setLiveStatus(0);
                if (mPresenter != null)
                    mPresenter.preparePush(bean);
                break;
        }
    }

    public void getPermission() {
        PermissionUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {
                requestToOpenGALLERY();
            }

            @Override
            public void forbitPermissons() {

            }
        });
    }

    /**
     * 因为dialog会自动消失,所以重新dialog的方法
     */
    private void setDialogPositiveClick() {
        descriptionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editValue = dialogEditText.getText().toString();
                if (editValue.length() > 15) {
                    ToastUtils.showShortToast("不能超过15个字符");
                    return;
                }
                if (!(StringUtil.isEmpty(editValue))) {
                    dialogEditValue = editValue;
                    descriptionTv.setText(dialogEditValue);
                }
                descriptionDialog.dismiss();
            }
        });
    }

    @Override
    public void preparePushSuccess() {
        Intent intent = new Intent(PreparePushActivity.this, PushActivity.class);
        startActivity(intent);
    }

    @Override
    public void preparePushFailure(String errorMessage) {
        Log.e("preparePushActivity", "preparePushFailure: " + errorMessage);
        ToastUtils.showShortToast(errorMessage);
    }

    @Override
    public void uploadIconSuccess(String path) {
        Glide.with(this).load(Api.FILE_HOST + path).into(addIconBtn);
        bean.setLiveIcon(path);
    }

    @Override
    public void uploadIconFailure(String errorMessage) {
        Log.e("preparePushActivity", "uploadIconFailure: " + errorMessage);
        ToastUtils.showShortToast("上传失败");
    }

    /**
     * 打开相册并获取图片
     */
    private void requestToOpenGALLERY() {
        Matisse.from(PreparePushActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP, MimeType.WEBP))
                .maxSelectable(1)
                .theme(R.style.Matisse_Zhihu)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
//            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //用户同意使用write
//                requestToOpenGALLERY();
//            } else {
//                //用户不同意，向用户展示该权限作用
//
////                finish();
//            }
//        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
