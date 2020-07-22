package com.gfq.grefreshview;

import java.util.List;

/**
 * @created GaoFuq
 * @Date 2020/7/17 17:07
 * @Descaption
 */
public interface RefreshVMEvent<T> {

    void onRefresh(List<T> list);

    void onLoadMore(List<T> list);

    List<T> getStateDataList();
}
