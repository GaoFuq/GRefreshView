package com.gfq.grefreshview.net;

public interface OnCallBack<T> {
    void onSuccess(T t);


    void onError(String e);


    /*void onComplete();*/


}