package com.example.lee.videoandroid.view.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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
    private HomeFragment homeFragment = new HomeFragment();
    private UserFragment userFragment = new UserFragment();
    private VideoFragment videoFragment = new VideoFragment();
    private TextView[] bottomTvList;
    private Fragment[] fragmentList;
    private Button[] bottomButtonList;
    private int fragmentStatusTag = 0;
    //    private final int[] bottomUnCheckImgList=new int[]{};
//    private final int[] bottomCheckedImgList=new int[]{};
    private FragmentManager manager;

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
        showFragment(fragmentStatusTag);
    }

    private void initFragment() {
        bottomTvList = new TextView[]{bottomHomeTv, bottomVideoTv, bottomUserTv};
        fragmentList = new Fragment[]{homeFragment, videoFragment, userFragment};
        bottomButtonList = new Button[]{bottomHomeBtn, bottomVideoBtn, bottomUserBtn};
        manager = this.getFragmentManager();
    }

    private void showFragment(int fragmentStatusTag) {
        final FragmentTransaction transaction = manager.beginTransaction();
        for (int i = 0; i < fragmentList.length; i++) {
            if (i != fragmentStatusTag) {
                transaction.hide(fragmentList[i]);
                bottomTvList[i].setTextColor(getResources().getColor(R.color.Silver));
//                bottomButtonList[i].setBackground();
            } else {
                if (!fragmentList[i].isAdded()) {
                    transaction.add(fragmentContainer.getId(), fragmentList[i]);
                }
                transaction.show(fragmentList[i]);
                bottomTvList[i].setTextColor(getResources().getColor(R.color.blue));
            }
        }
        transaction.commit();
        this.fragmentStatusTag = fragmentStatusTag;
    }

    @Override
    public void initData() {
        bottomHomeBtn.setOnClickListener(this);
        bottomUserBtn.setOnClickListener(this);
        bottomVideoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_home_btn:
                showFragment(0);
                break;
            case R.id.bottom_user_btn:
                showFragment(2);
                break;
            case R.id.bottom_video_btn:
                showFragment(1);
                break;
        }
    }
}
