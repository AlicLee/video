package com.example.lee.videoandroid.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseFragment;
import com.example.lee.videoandroid.base.Settings;
import com.example.lee.videoandroid.contact.HomeContact;
import com.example.lee.videoandroid.model.LiveBean;
import com.example.lee.videoandroid.model.UserBean;
import com.example.lee.videoandroid.presenter.HomePresenter;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.SizeUtil;
import com.example.lee.videoandroid.util.StringUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.login.LoginActivity;
import com.example.lee.videoandroid.view.main.adapter.HomeAdapter;
import com.example.lee.videoandroid.view.main.adapter.SpaceItemDecoration;
import com.example.lee.videoandroid.view.pull.PullActivity;
import com.example.lee.videoandroid.view.push.PreparePushActivity;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment<HomePresenter> implements View.OnClickListener, HomeContact.View {
    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerview;
    @BindView(R.id.home_addbtn)
    FloatingActionButton homeAddbtn;
    HomeAdapter adapter;
    @BindView(R.id.home_refreshLayout)
    SwipeRefreshLayout homeRefreshLayout;
    private int index = 0, pageSize = 10;
    private View emptyView;
    private ProgressBar refreshBtn;
    private TextView reasonView;
    private TextView errorView;
    private UserBean userBean;

    @Override
    public void initData(View view) {
        mPresenter.getLiveByPages(index, pageSize);
    }

    private void initRecyclerView() {
        adapter = new HomeAdapter(getActivity(), R.layout.item_homeadapter);
        homeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                index = 0;
                mPresenter.refresh(index, pageSize);
            }
        });
        final int SPACE_COUNT = 2, SPACE = 10;
        homeRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), SPACE_COUNT));
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(++index, pageSize);
            }
        }, homeRecyclerview);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LiveBean bean = (LiveBean) adapter.getData().get(position);
                String beanData = new Gson().toJson(bean);
//                if(bean.getLiveStatus()==1){
                Intent intent = new Intent(getActivity(), PullActivity.class);
                intent.putExtra("beanData", beanData);
                intent.putExtra("type", "live");
                startActivity(intent);
//                }
            }
        });
        homeRecyclerview.addItemDecoration(new SpaceItemDecoration(SizeUtil.dp2px(SPACE), SPACE_COUNT));
        homeRecyclerview.setAdapter(adapter);
    }

    @Override
    public void initView(View view) {
        initEmptyView();
        initRecyclerView();
        homeAddbtn.setOnClickListener(this);
    }

    private void initEmptyView() {
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty, null);
        refreshBtn = (ProgressBar) emptyView.findViewById(R.id.empty_refresh_view);
        reasonView = (TextView) emptyView.findViewById(R.id.empty_reason_txt);
        errorView = (TextView) emptyView.findViewById(R.id.empty_error_txt);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreEmptyViewStatus(true);
                mPresenter.getLiveByPages(index, pageSize);
            }
        });
    }

    @Override
    public int setLayoutView() {
        return R.layout.fragment_home;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_addbtn:
                String userBeanData = SharedPreUtil.getString(getActivity(), Settings.SharedPreUserKey, "");
                if (!StringUtil.isEmpty(userBeanData) && userBean == null) {
                    userBean = new Gson().fromJson(userBeanData, UserBean.class);
//                    break;
                }
                if (userBean != null) {
                    Intent intent = new Intent(getActivity(), PreparePushActivity.class);
                    startActivity(intent);
                    break;
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("source", "home");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void loadMoreSuccess(List<LiveBean> liveBeans) {
//        adapter.isUseEmpty(false);
//        adapter.isUseEmpty();
        if (liveBeans.size() < 10) {
            adapter.loadMoreEnd(false);
        } else {
            adapter.loadMoreComplete();
//            adapter.loadMoreEnd(true);
        }
//        if (adapter.isUpFetching()) {
//            adapter.setNewData(liveBeans);
//            adapter.setUpFetching(false);
//            return;
//        }
        adapter.addData(liveBeans);
    }


    @Override
    public void loadMoreFailure(String message) {
//        adapter.isUseEmpty(true);
        ToastUtils.showShortToast(message);
    }

    @Override
    public void refreshSuccess(List<LiveBean> liveBeans) {
//        isRefreshing = false;
        if (liveBeans.size() < 10) {
            adapter.loadMoreEnd(false);
        }
        adapter.getData().clear();
        adapter.addData(liveBeans);
        homeRefreshLayout.setRefreshing(false);
//        adapter.setUpFetching(false);
    }

    @Override
    public void refreshFailure(String message) {
//        adapter.setUpFetching(false);
        homeRefreshLayout.setRefreshing(false);
        ToastUtils.showShortToast(message);
    }

    @Override
    public void getLiveByPagesSuccess(List<LiveBean> liveBeans) {
        adapter.addData(liveBeans);
    }

    @Override
    public void getLiveByPagesFailure(String message) {
        restoreEmptyViewStatus(false);
        adapter.setEmptyView(emptyView);
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
