package com.gfq.grefreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import androidx.databinding.ViewDataBinding;

/**
 * @created GaoFuq
 * @Date 2020/6/16 12:54
 * @Descaption
 *
 * (HotSpecVM extends RefreshVM)
 * HotSpecVM vm = new ViewModelProvider(mActivity).get(HotSpecVM.class);
 *
 * RefreshViewHelper<HotSpecBean> helper = new RefreshViewHelper<>(mActivity, binding.container);
 *         RefreshView<HotSpecBean> refreshView = helper.getRefreshView();
 *         helper.createAdapter(R.layout.item_rv_forschools, BR.scl)
 *                 .handleRefresh()
 *                 .setCallBack(new RefreshViewHelper.AllCallBack<HotSpecBean>() {
 *                     @Override
 *                     public void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<HotSpecBean> adapter) {
 *                         Map<Object, Object> map = new HashMap<>();
 *                         map.put("id", id);
 *                         map.put("page", currentPage);
 *                         map.put("pageSize", pageSize);
 *                         APIService.call(APIService.api().getSchoolPosition(APIUtil.requestMap(map)), new OnCallBack<List<HotSpecBean>>() {
 *                             @Override
 *                             public void onSuccess(List<HotSpecBean> beans) {
 *                                 if (beans != null && beans.size() > 0) {
 *                                     adapter.loadMore(beans);
 *                                     layout.finishLoadMore(true);
 *                                     vm.onLoadMore(beans);
 *                                 } else {
 *                                     layout.finishLoadMoreWithNoMoreData();
 *                                 }
 *                             }
 *
 *                             @Override
 *                             public void onError(String e) {
 *
 *                             }
 *                         });
 *                     }
 *
 *                     @Override
 *                     public void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<HotSpecBean> adapter) {
 *                         Map<Object, Object> map = new HashMap<>();
 *                         map.put("id", id);
 *                         map.put("page", currentPage);
 *                         map.put("pageSize", pageSize);
 *                         APIService.call(APIService.api().getSchoolPosition(APIUtil.requestMap(map)), new OnCallBack<List<HotSpecBean>>() {
 *                             @Override
 *                             public void onSuccess(List<HotSpecBean> beans) {
 *                                 if (beans != null && beans.size() > 0) {
 *                                     refreshView.removeNoDataView();
 *                                     adapter.refresh(beans);
 *                                     vm.onRefresh(beans);
 *                                 } else {
 *                                     refreshView.showNoDataView();
 *                                 }
 *                                 layout.finishRefresh(true);
 *                             }
 *
 *                             @Override
 *                             public void onError(String e) {
 *
 *                             }
 *                         });
 *                     }
 *
 *                     @Override
 *                     public void onBindView(SuperBindingViewHolder holder, HotSpecBean data, int position) {
 *                     }
 *                 });
 *
 *         vm.start(refreshView);
 *
 */
public class RefreshViewHelper<T,VB extends ViewDataBinding> {
    private final Context context;
    private  AllCallBack<T,VB> callBack;
    private RefreshView<T,VB> refreshView;
    private BindingAdapter<T,VB> adapter;

    public RefreshView<T,VB> getRefreshView() {
        return refreshView;
    }

    public interface AllCallBack<T,VB extends ViewDataBinding>{
        void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, BindingAdapter<T,VB> adapter);
        void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, BindingAdapter<T,VB> adapter);
        void bindView(BindingViewHolder<VB> holder, T data, int position);
    }

    public void setCallBack(AllCallBack<T,VB> callBack) {
        this.callBack = callBack;
    }

    public RefreshViewHelper(Context context, ViewGroup parent) {
        this.context = context;
        refreshView = new RefreshView<>(context);
        parent.addView(refreshView);
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_nodata, null, false);
        refreshView.setNoDataPage(view);
        handleNet();
    }

    public RefreshViewHelper<T,VB> setMargin(int dpLeft,int dpTop,int dpRight,int dpBottom){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = DensityUtil.dp2px(dpLeft);
        params.rightMargin = DensityUtil.dp2px(dpRight);
        params.topMargin = DensityUtil.dp2px(dpTop);
        params.bottomMargin = DensityUtil.dp2px(dpBottom);
        refreshView.setLayoutParams(params);
        return this;
    }

    public RefreshViewHelper<T,VB> handleRefresh() {
        refreshView.setAdapter(adapter)
                .setRefreshViewListener(new RefreshView.RefreshViewListener<T,VB>() {
                    @Override
                    public void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, BindingAdapter<T,VB> adapter) {
                        callBack.requestLoadMore(currentPage,pageSize,layout,adapter);
                    }

                    @Override
                    public void requestLoadMore2(int currentPage, int pageSize) {

                    }

                    @Override
                    public void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, BindingAdapter<T,VB> adapter) {
                        callBack.requestRefresh(currentPage,pageSize,layout,adapter);
                    }

                    @Override
                    public void requestRefresh2(int currentPage, int pageSize) {

                    }

                    @Override
                    public void bindView(BindingViewHolder<VB> holder, List<T> dataList, int position) {

                    }
                });

        return  this;
    }



    public RefreshViewHelper<T,VB> createAdapter(int itemLayout,int var) {
        adapter = new BindingAdapter<T,VB>(context,var) {

            @Override
            public void onBindView(BindingViewHolder<VB> holder, int position) {
                callBack.bindView(holder,getDataList().get(position),position);
            }

            @Override
            public int getLayoutId() {
                return itemLayout;
            }
        };
        return this;
    }




    public RefreshViewHelper<T,VB> handleNet() {
        //处理网络断开
        refreshView.setNetDisconnectedView(new NetDisconnectedView(context) {
            @Override
            protected View setRetryView() {
                View contentView = getContentView();
                return contentView.findViewById(R.id.tv_retry);
            }

            @Override
            protected SmartRefreshLayout setSmartRefreshLayout() {
                return refreshView.getSmartRefreshLayout();
            }

            @Override
            protected int setContentView() {
                return R.layout.refresh_no_net;
            }
        });
        return this;
    }


}
