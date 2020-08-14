package com.gfq.grefreshview;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder基类
 */
public class SuperBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public  T getBinding() {
        return binding;
    }

    private final T binding;

    public SuperBindingViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

}
