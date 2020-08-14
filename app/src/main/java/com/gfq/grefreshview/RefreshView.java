package com.gfq.grefreshview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;


/**
 * create by 高富强
 * on {2019/11/5} {10:30}
 * desctapion: 实现自动刷新加载数据
 */
public class RefreshView<T> extends FrameLayout {

    private Context context;

    private int currentPage = 0;//当前页
    private int pageSize = 10;//每页数据条数
    private int totalPage = 100;//总页数
    private int totalCount = 1000;//数据总量
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private FrameLayout container;
    private RVBindingAdapter<T> adapter;
    private LinearLayoutManager linearLayoutManager;

    private NetDisconnectedView netDisconnectedView;
    private View noDataView;
    private boolean isAutoRefreshOnStart = true;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SmartRefreshLayout getSmartRefreshLayout() {
        return smartRefreshLayout;
    }

    public void setNetDisconnectedView(NetDisconnectedView netDisconnectedView) {
        this.netDisconnectedView = netDisconnectedView;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public RefreshView(@NonNull Context context) {
        this(context, null);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initThis();
    }

    public void setNoDataView(View noDataView) {
        this.noDataView = noDataView;
    }

    public void showNoDataView() {
        removeNoDataView();
        container.addView(noDataView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void removeNoDataView() {
        if (noDataView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) noDataView.getParent();
        if (parent != null) {
            container.removeView(noDataView);
        }
    }

    public void setData(List<T> data){
        if(data==null)return;
        adapter.refresh(data);
        currentPage = 2;
    }

    private void initThis() {
        View view = inflate(context, R.layout.refreshview, this);
        smartRefreshLayout = view.findViewById(R.id.smartrefresh);
        recyclerView = view.findViewById(R.id.recycleView);
        container = view.findViewById(R.id.container);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);//默认垂直LinearLayoutManager

        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(context));

        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(context));


        //默认可以加载更多，可以下拉刷新
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isNetworkConnected(context.getApplicationContext())) {
                    removeNetDisconnectedView();
                    doLoadMore(refreshLayout);
                } else {
                    refreshLayout.finishLoadMore(false);
                    addNetDisconnectedView(Type.loadMore);
                }

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isNetworkConnected(context.getApplicationContext())) {
                    removeNetDisconnectedView();
                    doRefresh(refreshLayout);
                } else {
                    refreshLayout.finishRefresh(false);
                    addNetDisconnectedView(Type.refresh);
                }
            }
        });
    }


    private void doRefresh(RefreshLayout refreshLayout) {
        if (refreshViewListener != null) {
            currentPage = 1;
            if (adapter == null) {
                return;
            }
            refreshViewListener.requestRefresh(currentPage, pageSize, refreshLayout, adapter);

        }
    }

    private void doLoadMore(RefreshLayout refreshLayout) {
        if (refreshViewListener != null) {
            currentPage++;
            if (currentPage > totalPage) {
                currentPage = totalPage;
                refreshLayout.finishLoadMoreWithNoMoreData();
                return;
            }
            if (adapter == null) {
                return;
            }
            refreshViewListener.requestLoadMore(currentPage, pageSize, refreshLayout, adapter);
        }
    }

    private void removeNetDisconnectedView() {
        if (netDisconnectedView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) netDisconnectedView.getParent();
        if (parent != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    container.removeView(netDisconnectedView);
                }
            }, 500);
        }
        if (netListener != null) {
            netListener.onAvailable();
        }
    }

    private void addNetDisconnectedView(Type type) {
        if (netDisconnectedView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) netDisconnectedView.getParent();
        if (parent == null) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(netDisconnectedView, params);
            netDisconnectedView.setType(type);
        }
        if (netListener != null) {
            netListener.onLoseNet();
        }
    }


    public void setCanRefresh(boolean b) {
        smartRefreshLayout.setEnableRefresh(b);
    }

    public void setCanLoadMore(boolean b) {
        smartRefreshLayout.setEnableLoadMore(b);
    }

    public void autoRefresh() {
        if (smartRefreshLayout != null)
            smartRefreshLayout.autoRefresh();
    }

    public void autoLoadMore() {
        if (smartRefreshLayout != null)
            smartRefreshLayout.autoLoadMore();
    }


    public RefreshView<T> setH_LinearLayoutManager() {
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        return this;
    }

    public RefreshView<T> setGridLayoutManager(int spanCount, int rowSpacing, int columnSpacing) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(spanCount, DensityUtil.dp2px(rowSpacing), DensityUtil.dp2px(columnSpacing)));
        return this;
    }


    public RefreshView<T> setGridLayoutManager(int column, int orientation) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, column, orientation, false));
        return this;
    }


    public RefreshView<T> setAdapter(RVBindingAdapter<T> adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        return this;
    }

    public RefreshView<T> setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
        return this;
    }

    public interface NetListener {
        void onLoseNet();

        void onAvailable();
    }

    private NetListener netListener;

    public void setNetListener(NetListener netListener) {
        this.netListener = netListener;
    }


    public interface RefreshViewListener<T> {
        void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter);

        void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter);
    }

    private RefreshViewListener<T> refreshViewListener;

    public void setRefreshViewListener(RefreshViewListener<T> refreshViewListener) {
        this.refreshViewListener = refreshViewListener;
    }

    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = null;
            if (mConnectivityManager != null) {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            }
            //获取连接对象
            if (mNetworkInfo != null) {
                //判断是TYPE_MOBILE网络
                if (ConnectivityManager.TYPE_MOBILE == mNetworkInfo.getType()) {
//                    LogManager.i("AppNetworkMgr", "网络连接类型为：TYPE_MOBILE");
                    //判断移动网络连接状态
                    NetworkInfo.State STATE_MOBILE = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                    if (STATE_MOBILE == NetworkInfo.State.CONNECTED) {
//                        LogManager.i("AppNetworkMgrd", "网络连接类型为：TYPE_MOBILE, 网络连接状态CONNECTED成功！");
                        return mNetworkInfo.isAvailable();
                    }
                }
                //判断是TYPE_WIFI网络
                if (ConnectivityManager.TYPE_WIFI == mNetworkInfo.getType()) {
//                    LogManager.i("AppNetworkMgr", "网络连接类型为：TYPE_WIFI");
                    //判断WIFI网络状态
                    NetworkInfo.State STATE_WIFI = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                    if (STATE_WIFI == NetworkInfo.State.CONNECTED) {
//                        LogManager.i("AppNetworkMgr", "网络连接类型为：TYPE_WIFI, 网络连接状态CONNECTED成功！");
                        return mNetworkInfo.isAvailable();
                    }
                }
            }
        }
        return false;
    }


}
