package com.gfq.grefreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public abstract class NetDisconnectedView extends FrameLayout  {
    private Type type;
    private Context context;
    private SmartRefreshLayout smartRefreshLayout;
    private View contentView;
    private View retryView;

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public View getContentView() {
        return contentView;
    }

    public NetDisconnectedView(@NonNull Context context) {
        this(context, null);
    }

    public NetDisconnectedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetDisconnectedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initThis();
    }

    private void initThis() {
//        setBackgroundColor(Color.WHITE);
        contentView = inflate(context, setContentView(), this);
        retryView = setRetryView();
        smartRefreshLayout=setSmartRefreshLayout();
    }

    protected abstract View setRetryView();


    protected abstract SmartRefreshLayout setSmartRefreshLayout ();

    protected abstract @LayoutRes
    int setContentView();


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = retryView.getX();
        float y = retryView.getY();
        int w = retryView.getWidth();
        int h = retryView.getHeight();
        boolean touched = event.getX() < x + w + 100 && event.getX() > x - 100 && event.getY() < y + h + 100 && event.getY() > y - 100;
        if (type == Type.refresh && touched) {
            smartRefreshLayout .autoRefresh();
        }
        if (type == Type.loadMore && touched) {
            smartRefreshLayout.autoLoadMore();
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


}