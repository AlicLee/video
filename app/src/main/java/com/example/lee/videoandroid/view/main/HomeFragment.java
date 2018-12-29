package com.example.lee.videoandroid.view.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.view.login.LoginActivity;
import com.example.lee.videoandroid.base.BaseFragment;
import com.example.lee.videoandroid.contact.HomeContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.presenter.HomePresenter;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.main.adapter.HomeAdapter;

import java.util.List;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements View.OnClickListener, HomeContact.View {

    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;
    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerview;
    @BindView(R.id.home_addbtn)
    FloatingActionButton homeAddbtn;
    HomeAdapter adapter;
    private boolean haveMoreData = true;
    private int index = 0, pageSize = 10;

    @Override
    public void initData(View view) {
        if (!(mPresenter instanceof HomePresenter)) {
            return;
        }
        final HomePresenter homePresenter = (HomePresenter) mPresenter;
        homePresenter.getLiveByPages(index, pageSize);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                homePresenter.getLiveByPages(index++, pageSize);
            }
        }, homeRecyclerview);
    }

    @Override
    public void initView(View view) {
        homeAddbtn.setOnClickListener(this);
    }

    @Override
    public int setLayoutView() {
        return R.layout.fragment_home;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_addbtn:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void getLiveSuccess(List<LiveBean> liveBeans) {
        if (adapter == null) {
            //第一次获取数据
            adapter = new HomeAdapter(getActivity(), R.layout.item_homeadapter, liveBeans);
            homeRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            homeRecyclerview.setAdapter(adapter);
            if (liveBeans.size() < 10) {
                adapter.setEnableLoadMore(false);
            } else {
                adapter.setEnableLoadMore(true);
            }
        } else {
            if (liveBeans.size() < 10) {
                adapter.loadMoreEnd();
            } else {
                adapter.loadMoreComplete();
            }
            adapter.addData(liveBeans);
        }
    }

    @Override
    public void getLiveFailure(String message) {
        ToastUtils.showShortToast(message);
    }
}
