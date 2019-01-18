package com.example.lee.videoandroid.view.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class HomeFragment extends BaseFragment<HomePresenter> implements View.OnClickListener, HomeContact.View {
    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerview;
    @BindView(R.id.home_addbtn)
    FloatingActionButton homeAddbtn;
    HomeAdapter adapter;
    private boolean haveMoreData = true;
    private int index = 0, pageSize = 10;
    private View emptyView;
    private ProgressBar refreshBtn;
    private TextView reasonView;
    private TextView errorView;

    @Override
    public void initData(View view) {
        mPresenter.getLiveByPages(index, pageSize);
        adapter = new HomeAdapter(getActivity(), R.layout.item_homeadapter);
        homeRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getLiveByPages(index++, pageSize);
            }
        }, homeRecyclerview);
//        adapter.bindToRecyclerView(homeRecyclerview);
//        homeRecyclerview.setAdapter(adapter);
    }

    @Override
    public void initView(View view) {
        initEmptyView();
        homeAddbtn.setOnClickListener(this);
    }

    private void initEmptyView() {
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty, null);
        refreshBtn = (ProgressBar) emptyView.findViewById(R.id.empty_refresh_view);
        reasonView = (TextView) emptyView.findViewById(R.id.empty_reason_txt);
        errorView = (TextView) emptyView.findViewById(R.id.empty_error_txt);
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
        if (liveBeans.size() < 10) {
            adapter.loadMoreEnd();
            adapter.loadMoreEnd(false);
        } else {
            adapter.loadMoreComplete();
            adapter.loadMoreEnd(true);
        }
        adapter.addData(liveBeans);
    }

    @Override
    public void getLiveFailure(String message) {
        adapter.setEmptyView(emptyView);
        ToastUtils.showShortToast(message);
    }

    private void restoreEmptyViewStatus(boolean isShowProgressBar) {
        if (isShowProgressBar) {
            refreshBtn.setVisibility(View.VISIBLE);
            reasonView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.INVISIBLE);
        } else {
            refreshBtn.setVisibility(View.INVISIBLE);
            reasonView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
    }
}
