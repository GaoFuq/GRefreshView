package com.gfq.grefreshview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfq.gdatabind.BindingAdapter;
import com.gfq.gdatabind.BindingViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Collections;
import java.util.List;


/**
 * create by 高富强
 * on {2019/11/5} {10:30}
 * desctapion: 实现自动刷新加载数据
 *
 * public class MainActivity extends AppCompatActivity {
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *
 *          // 1. new 对象
 *         final RefreshView<Bean, ItemTestBinding> refreshView = new RefreshView<>(this);
 *         setContentView(refreshView);
 *
 *
 *         final List<Bean> list = new ArrayList<>();
 *
 *         for (int i = 0; i < 10; i++) {
 *             Bean bean = new Bean();
 *             bean.setName("初始数据 --> " + i);
 *             list.add(bean);
 *         }
 *
 *          //2.初始化 adapter
 *         refreshView.createAdapter(R.layout.item_test, 0)
 *         //3. 设置回调
 *                    .setRefreshViewListener(new RefreshView.RefreshViewListener<Bean, ItemTestBinding>() {
 *             @Override
 *             public void requestLoadMore(int currentPage, int pageSize) {
 *                 List<Bean> list = new ArrayList<>();
 *                 for (int i = 0; i < 10; i++) {
 *                     Bean bean = new Bean();
 *                     bean.setName("初始数据 --> " + i);
 *                     list.add(bean);
 *                 }
 *                 refreshView.setLoadMoreDataList(list);
 *             }
 *
 *
 *             @Override
 *             public void requestRefresh(int currentPage, int pageSize) {
 *                 refreshView.setRefreshDataList(null);
 *             }
 *
 *             @Override
 *             public void bindView(BindingViewHolder<ItemTestBinding> holder, final List<Bean> dataList, final int position) {
 *                 ItemTestBinding binding = holder.getBinding();
 *                 binding.xx.setText(dataList.get(position).getName());
 *                 if (dataList.get(position).isSelected()) {
 *                     holder.itemView.setBackgroundColor(0xff456852);
 *                 } else {
 *                     holder.itemView.setBackgroundColor(0xffffffff);
 *                 }
 *                 holder.itemView.setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                         dataList.get(position).setSelected(!dataList.get(position).isSelected());
 *                         refreshView.notifyAdapterDataSetChanged();
 *                     }
 *                 });
 *             }
 *         });
 *         refreshView.setNoDataPage(R.layout.item_test);
 *         refreshView.autoRefresh();
 *     }
 *
 *
 *     public static class Bean {
 *         boolean selected;
 *         String name;
 *
 *         public boolean isSelected() {
 *             return selected;
 *         }
 *
 *         public Bean setSelected(boolean selected) {
 *             this.selected = selected;
 *             return this;
 *         }
 *
 *         public String getName() {
 *             return name;
 *         }
 *
 *         public Bean setName(String name) {
 *             this.name = name;
 *             return this;
 *         }
 *     }
 * }
 */
public class RefreshView<T, VB extends ViewDataBinding> extends FrameLayout {

    private Context context;

    private int startPage = 1;
    private int currentPage = 1;//当前页
    private int pageSize = 10;//每页数据条数
    private int totalPage = 100;//总页数
    private int totalCount = 1000;//数据总量
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private FrameLayout container;
    private BindingAdapter<T, VB> adapter;
    private RefreshView<T, VB> refreshView;
    private LinearLayoutManager linearLayoutManager;

    private View noNetPage;
    private View noDataPage;
    private View errorPage;

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public BindingAdapter<T, VB> getAdapter() {
        return adapter;
    }

    public SmartRefreshLayout getSmartRefreshLayout() {
        return smartRefreshLayout;
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
        refreshView = this;
        initThis();
    }

    public void setNoNetPage(View noNetPage) {
        this.noNetPage = noNetPage;
    }

    public void setNoDataPage(View noDataPage) {
        this.noDataPage = noDataPage;
    }

    public void setNoDataPage(@LayoutRes int noDataPageLayout) {
        this.noDataPage = LayoutInflater.from(context).inflate(noDataPageLayout, null, false);
    }

    //在bindView回调中，改变数据源，可以调用该方法刷新界面
    public void notifyAdapterDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public void showNoDataView() {
        removeNoDataView();
        container.addView(noDataPage, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void removeNoDataView() {
        if (noDataPage == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) noDataPage.getParent();
        if (parent != null) {
            container.removeView(noDataPage);
        }
    }

    public void setDataList(List<T> data) {
        if (data == null) return;
        adapter.setDataList(data);
    }

    private void initThis() {
        View view = inflate(context, R.layout.refreshview_nested, this);
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
                    addNoNetPage(Type.loadMore);
                }

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isNetworkConnected(context.getApplicationContext())) {
                    removeNetDisconnectedView();
                    doRefresh(refreshLayout);
                } else {
                    refreshLayout.finishRefresh(false);
                    addNoNetPage(Type.refresh);
                }
            }
        });
    }

    public RefreshView<T, VB> createAdapter(int itemLayout, int var) {
        adapter = new BindingAdapter<T, VB>(context, var) {

            @Override
            public void onBindView(BindingViewHolder<VB> holder, int position) {
                refreshViewListener.bindView(holder, getDataList(), position);
            }

            @Override
            public int getLayoutId() {
                return itemLayout;
            }
        };
        recyclerView.setAdapter(adapter);
        return this;
    }


    private void doRefresh(RefreshLayout refreshLayout) {
        if (refreshViewListener != null) {
            currentPage = 1;
            if (adapter == null) {
                return;
            }
//            refreshViewListener.requestRefresh(currentPage, pageSize, refreshLayout, adapter);
            refreshViewListener.requestRefresh(currentPage, pageSize);

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
//            refreshViewListener.requestLoadMore(currentPage, pageSize, refreshLayout, adapter);
            refreshViewListener.requestLoadMore(currentPage, pageSize);
        }
    }

    private RefreshVM<T> vm;

    public RefreshVM<T> getVm() {
        return vm;
    }

    public RefreshView<T, VB> setVm(RefreshVM<T> vm) {
        this.vm = vm;
        return this;
    }

    public void setRefreshDataList(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            removeNoDataView();
            adapter.setDataList(dataList);
            if (vm != null) {
                vm.onRefresh(dataList);
            }
        } else {
            refreshView.showNoDataView();
            adapter.setDataList(Collections.emptyList());
            if (vm != null) {
                vm.onRefresh(Collections.emptyList());
            }
        }
        smartRefreshLayout.finishRefresh(true);
    }

    public void setLoadMoreDataList(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            removeNoDataView();
            adapter.addAll(dataList);
            smartRefreshLayout.finishLoadMore(true);
            if (vm != null) {
                vm.onLoadMore(dataList);
            }
        } else {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    public void setErrorPage(View errorPage) {
        this.errorPage = errorPage;
    }


    private void removeNetDisconnectedView() {
        if (noNetPage == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) noNetPage.getParent();
        if (parent != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    container.removeView(noNetPage);
                }
            }, 500);
        }
        if (netListener != null) {
            netListener.onAvailable();
        }
    }

    private void addNoNetPage(Type type) {
        if (noNetPage == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) noNetPage.getParent();
        if (parent == null) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(noNetPage, params);
//            noNetPage.setType(type);
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


    public RefreshView<T, VB> setH_LinearLayoutManager() {
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        return this;
    }

    public RefreshView<T, VB> setGridLayoutManager(int spanCount, int rowSpacing, int columnSpacing) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(spanCount, DensityUtil.dp2px(rowSpacing), DensityUtil.dp2px(columnSpacing)));
        return this;
    }

    public RefreshView<T, VB> setAdapter(BindingAdapter<T, VB> adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        return this;
    }

    public RefreshView<T, VB> setLayoutManager(RecyclerView.LayoutManager manager) {
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


    public interface RefreshViewListener<T, VB extends ViewDataBinding> {

        void requestLoadMore(int currentPage, int pageSize);

        void requestRefresh(int currentPage, int pageSize);

        void bindView(BindingViewHolder<VB> holder, List<T> dataList, int position);
    }

    private RefreshViewListener<T, VB> refreshViewListener;

    public void setRefreshViewListener(RefreshViewListener<T, VB> refreshViewListener) {
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
