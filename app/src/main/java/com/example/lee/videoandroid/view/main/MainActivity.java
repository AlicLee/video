package com.example.lee.videoandroid.view.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.util.VideoUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.lineView)
    View lineView;
    @BindView(R.id.bottom_home_btn)
    Button bottomHomeBtn;
    @BindView(R.id.bottom_video_btn)
    Button bottomVideoBtn;
    @BindView(R.id.bottom_user_btn)
    Button bottomUserBtn;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.bottom_home_tv)
    TextView bottomHomeTv;
    @BindView(R.id.bottom_video_tv)
    TextView bottomVideoTv;
    @BindView(R.id.bottom_user_tv)
    TextView bottomUserTv;
    @BindView(R.id.bottom_home_layout)
    RelativeLayout bottomHomeLayout;
    @BindView(R.id.bottom_video_layout)
    RelativeLayout bottomVideoLayout;
    @BindView(R.id.bottom_person_layout)
    RelativeLayout bottomPersonLayout;
    private HomeFragment homeFragment = new HomeFragment();
    private UserFragment userFragment = new UserFragment();
    private VideoFragment videoFragment = new VideoFragment();
    private TextView[] bottomTvList;
    private Fragment[] fragmentList;
    private final String[] fragmentTitleNameList = new String[]{"推荐", "视频", "我的"};
    private Button[] bottomButtonList;
    private int fragmentStatusTag = 0;
    private final int[] outlineDrawableList = new int[]{R.drawable.home_outline, R.drawable.video_outline, R.drawable.person_outline};
    private final int[] normalDrawableList = new int[]{R.drawable.home, R.drawable.video, R.drawable.person};
    //    private final int[] bottomUnCheckImgList=new int[]{};
    //    private final int[] bottomCheckedImgList=new int[]{};
    private FragmentManager manager;
    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFragment(fragmentStatusTag);
    }

    private void initFragment() {
        bottomTvList = new TextView[]{bottomHomeTv, bottomVideoTv, bottomUserTv};
        fragmentList = new Fragment[]{homeFragment, videoFragment, userFragment};
        bottomButtonList = new Button[]{bottomHomeBtn, bottomVideoBtn, bottomUserBtn};
        manager = this.getSupportFragmentManager();
    }

    private void showFragment(int fragmentStatusTag) {
        final FragmentTransaction transaction = manager.beginTransaction();
        //遍历fragment
        for (int i = 0; i < fragmentList.length; i++) {
            //如果不是选中的fragment复原状态
            if (i != fragmentStatusTag) {
                transaction.hide(fragmentList[i]);
                bottomTvList[i].setTextColor(getResources().getColor(R.color.Gainsboro));
                bottomButtonList[i].setBackgroundResource(outlineDrawableList[i]);
            } else {
                //如果是选中的fragment设置状态
                if (!fragmentList[i].isAdded()) {
                    transaction.add(fragmentContainer.getId(), fragmentList[i]);
                }
                transaction.show(fragmentList[i]);
                bottomTvList[i].setTextColor(getResources().getColor(R.color.blue));
                bottomButtonList[i].setBackgroundResource(normalDrawableList[i]);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar == null)
                    return;
                actionBar.setTitle(fragmentTitleNameList[i]);
//                if (i == 1)
////                    menuItem.setVisible(true);
////                else
////                    menuItem.setVisible(false);
            }
        }
        transaction.commit();
        this.fragmentStatusTag = fragmentStatusTag;
    }


    //
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menuItem = menu.findItem(R.id.actionbar_uploadbtn);
//        getMenuInflater().inflate(R.menu.video_actionbar, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (fragmentList[fragmentStatusTag].isVisible() && fragmentStatusTag == 1) {
//            menu.findItem(R.id.actionbar_uploadbtn).setVisible(true);
//        } else if (fragmentList[fragmentStatusTag].isVisible()) {
//            menu.findItem(R.id.actionbar_uploadbtn).setVisible(false);
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void initData() {
//        bottomHomeBtn.setOnClickListener(this);
//        bottomUserBtn.setOnClickListener(this);
//        bottomVideoBtn.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.bottom_home_btn:
//                showFragment(0);
//                break;
//            case R.id.bottom_user_btn:
//                showFragment(2);
//                break;
//            case R.id.bottom_video_btn:
//                showFragment(1);
//                break;
//        }
//    }

    @OnClick({R.id.bottom_home_layout, R.id.bottom_video_layout, R.id.bottom_person_layout, R.id.bottom_home_btn, R.id.bottom_user_btn, R.id.bottom_video_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bottom_home_layout:
            case R.id.bottom_home_btn:
                showFragment(0);
                break;
            case R.id.bottom_video_layout:
            case R.id.bottom_video_btn:
                showFragment(1);
                break;
            case R.id.bottom_person_layout:
            case R.id.bottom_user_btn:
                showFragment(2);
                break;
        }
    }
}
