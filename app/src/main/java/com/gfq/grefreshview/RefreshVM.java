package com.gfq.grefreshview;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @created GaoFuq
 * @Date 2020/7/17 17:03
 * @Descaption
 */
public class RefreshVM<T> extends ViewModel implements RefreshVMEvent<T> {
    private List<T> dataList;

    @Override
    public void onRefresh(List<T> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
    }

    @Override
    public void onLoadMore(List<T> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.addAll(list);
    }

    @Override
    public List<T> getStateDataList() {
        return dataList;
    }

    public void start(RefreshView<T> refreshView) {
       
        List<T> beans = getStateDataList();
        if (beans == null) {
            refreshView.autoRefresh();
        } else {
            refreshView.setData(beans);
        }
    }

}
