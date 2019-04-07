package com.example.lee.videoandroid.view.main;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.lee.videoandroid.R;
import com.example.lee.videoandroid.base.BaseFragment;
import com.example.lee.videoandroid.contact.VideoContact;
import com.example.lee.videoandroid.download.DownloadService;
import com.example.lee.videoandroid.model.VideoBean;
import com.example.lee.videoandroid.network.Api;
import com.example.lee.videoandroid.presenter.VideoPresenter;
import com.example.lee.videoandroid.util.PermissionUtils;
import com.example.lee.videoandroid.util.SharedPreUtil;
import com.example.lee.videoandroid.util.ToastUtils;
import com.example.lee.videoandroid.view.main.adapter.VideoAdapter;
import com.example.lee.videoandroid.view.pull.PullActivity;
import com.example.lee.videoandroid.view.video.UploadVideoActivity;
import com.google.gson.Gson;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static android.os.Environment.DIRECTORY_MOVIES;

public class VideoFragment extends BaseFragment<VideoPresenter> implements VideoContact.View {

    @BindView(R.id.video_recyclerview)
    RecyclerView videoRecyclerview;
    @BindView(R.id.video_refreshLayout)
    SwipeRefreshLayout videoRefreshLayout;
    private int pageIndex = 0, pageSize = 10;
    private TextView reasonView;
    private TextView errorView;
    private View emptyView;
    private ProgressBar refreshBtn;
    private VideoAdapter adapter;
    private final String TAG = VideoFragment.class.getSimpleName();
    private HashMap<String, DownloadTask> taskMap = new HashMap();

    @Override
    public void initData(View view) {
        requestPermission();
    }

    public void requestPermission() {
        PermissionUtils.getInstance().checkPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionUtils.IPermissionsResult() {
                    @Override
                    public void passPermissons() {

                    }

                    @Override
                    public void forbitPermissons() {
                        requestPermission();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(requireActivity(), requestCode, permissions, grantResults);
    }

    @Override
    public void initView(View view) {
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new VideoAdapter(getActivity(), R.layout.item_videoadapter);
        videoRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(++pageIndex, pageSize);
            }
        }, videoRecyclerview);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PullActivity.class);
                intent.putExtra("beanData", new Gson().toJson(adapter.getData().get(position)));
                intent.putExtra("type", "video");
                startActivity(intent);
            }
        });
        adapter.setOnItemDownloadListener(new VideoAdapter.OnItemDownloadListener() {
            @Override
            public void onItemDownload(BaseQuickAdapter adapter, VideoBean videoItem, int position) {
//                long downloadId = SharedPreUtil.getLong(getActivity(), Api.FILE_HOST + videoItem.getPath(), -1L);
//                if (downloadId != -1) {
//                    downloadFile(videoItem);
//                } else {
//
//                }
//                bindService(Api.FILE_HOST + videoItem.getPath(), videoItem.getName());
                String name = videoItem.getPath().substring(videoItem.getPath().lastIndexOf("/"));
                DownloadTask task = createDownloadTask(Api.FILE_HOST + videoItem.getPath(), name, videoItem.getName());
//                taskMap.put(Api.FILE_HOST + videoItem.getPath(), task);
            }
        });
        videoRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 0;
                mPresenter.refresh(pageIndex, pageSize);
            }
        });
        videoRecyclerview.setAdapter(adapter);
        mPresenter.getVideoByPages(pageIndex, pageSize);
    }

    /**
     * 创建下载任务
     *
     * @param fileUrl
     * @param name
     * @return
     */
    public DownloadTask createDownloadTask(String fileUrl, String name, final String title) {
        DownloadTask task = new DownloadTask.Builder(fileUrl, Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).toString(), name)
                .setMinIntervalMillisCallbackProcess(30)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
        task.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                Log.d(TAG, "taskStart: ");
                ToastUtils.showShortToast(title + "已经开始下载");
            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
//                Log.d(TAG, "fetchProgress:" + (increaseBytes / blockIndex));
            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
//                if (task.getFile() != null)
//                Log.d(TAG, "taskEnd:filePath:" + task.getFile().getPath() + "cause:" + cause + "realCause:" + realCause);
                if (cause.name().equals(EndCause.COMPLETED.toString())) {
                    ToastUtils.showShortToast(title + "下载已完成,保存路径为:" + task.getFile().getPath());
                }
            }
        });
        return task;
    }

    @Override
    public int setLayoutView() {
        return R.layout.fragment_video;
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
                mPresenter.getVideoByPages(pageIndex, pageSize);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.video_actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_uploadbtn:
                Intent intent = new Intent(getActivity(), UploadVideoActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void getVideoByPagesSuccess(List<VideoBean> videoBeanList) {
        adapter.addData(videoBeanList);
    }

    @Override
    public void getVideoByPagesFailure(String message) {
        ToastUtils.showShortToast(message);
    }

    @Override
    public void onRefreshSuccess(List<VideoBean> videoBeanList) {
        if (videoBeanList.size() < 10) {
            adapter.loadMoreEnd(false);
        }
        adapter.getData().clear();
        adapter.addData(videoBeanList);
        videoRefreshLayout.setRefreshing(false);
//        if(videoBeanList.size()<pageSize){
//        }
    }

    @Override
    public void onRefreshFailure(String message) {
        ToastUtils.showShortToast(message);
        videoRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMoreSuccess(List<VideoBean> videoBeanList) {
        if (videoBeanList.size() < pageSize) {
            adapter.loadMoreEnd(false);
        } else {
            adapter.loadMoreComplete();
        }
        adapter.addData(videoBeanList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoadMoreFailure(String message) {
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
