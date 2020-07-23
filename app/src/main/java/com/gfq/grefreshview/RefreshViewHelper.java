package com.gfq.grefreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

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
public class RefreshViewHelper<T> {
    private final Context context;
    private  AllCallBack<T> callBack;
    private RefreshView<T> refreshView;
    private RVBindingAdapter<T> adapter;

    public RefreshView<T> getRefreshView() {
        return refreshView;
    }

    public interface AllCallBack<T>{
        void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter);
        void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter);
        void setPresentor(SuperBindingViewHolder holder, T data, int position);
    }

    public void setCallBack(AllCallBack<T> callBack) {
        this.callBack = callBack;
    }

    public RefreshViewHelper(Context context, ViewGroup parent) {
        this.context = context;
        refreshView = new RefreshView<>(context);
        parent.addView(refreshView);
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_nodata, null, false);
        refreshView.setNoDataView(view);
        handleNet();
    }

    public RefreshViewHelper<T> setMargin(int dpLeft,int dpTop,int dpRight,int dpBottom){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = DensityUtil.dp2px(dpLeft);
        params.rightMargin = DensityUtil.dp2px(dpRight);
        params.topMargin = DensityUtil.dp2px(dpTop);
        params.bottomMargin = DensityUtil.dp2px(dpBottom);
        refreshView.setLayoutParams(params);
        return this;
    }

    public RefreshViewHelper<T> handleRefresh() {
        refreshView.setAdapter(adapter)
                .setRefreshViewListener(new RefreshView.RefreshViewListener<T>() {
                    @Override
                    public void requestLoadMore(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter) {
                        callBack.requestLoadMore(currentPage,pageSize,layout,adapter);
                    }

                    @Override
                    public void requestRefresh(int currentPage, int pageSize, RefreshLayout layout, RVBindingAdapter<T> adapter) {
                        callBack.requestRefresh(currentPage,pageSize,layout,adapter);
                    }
                });

        return  this;
    }



    public RefreshViewHelper<T> createAdapter(int itemLayout,int var) {
        adapter = new RVBindingAdapter<T>(context,var) {

            @Override
            public void onBindView(SuperBindingViewHolder holder, int position) {
                callBack.setPresentor(holder,getDataList().get(position),position);
            }

            @Override
            public int getLayoutId() {
                return itemLayout;
            }
        };
        return this;
    }




    public RefreshViewHelper<T> handleNet() {
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
