package com.gfq.grefreshview.net;

import android.os.Environment;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RefreshViewAPIService {
    private RefreshViewAPI apiInterface;
    private final long cacheSize = 1024 * 1024 * 50;
    private String cacheDirectory = Environment.getExternalStorageDirectory() + "/refresh";
    private Cache cache = new Cache(new File(cacheDirectory), cacheSize);

    public static void init(APIConfig apiConfig){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间
                .writeTimeout(30, TimeUnit.SECONDS)// 设置写入超时时间
                .readTimeout(30, TimeUnit.SECONDS)// 设置读取数据超时时间
                .retryOnConnectionFailure(true)// 设置进行连接失败重试
                .addInterceptor(loggingInterceptor)
                .addInterceptor(apiConfig.getHeaderInterceptor())
                .cache(new Cache(new File(apiConfig.getCacheDir()), apiConfig.getCacheSize()))// 设置缓存
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(mClient)
                .baseUrl(apiConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
//        apiInterface = retrofit.create(RefreshViewAPI.class);
    }



//    public static void setToken(String tt) {
//        token = tt;
//    }
//
//    public static RefreshViewAPI api() {
//        return apiInterface;
//    }
//
//    public static <T> void call(Observable<API<T>> apiObservable, OnCallBack<T> onCallBack) {
//        Log.e("APIService token = ", token);
//        apiObservable.compose(upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .map(tApi -> {
//                    msg = tApi.getMsg();
//                    if (tApi.getCode() == 200 || tApi.getCode() == 204) {
//                        return tApi.getData();
//                    } else if (tApi.getCode() == 401) {
//                        HandleException.getInstance().Handle(tApi.getMsg(), tApi.getCode());
//                        return null;
//                    } else {
//                        throw new RuntimeException("xxx");
//                    }
//                })).debounce(1, TimeUnit.SECONDS)
//                .subscribe(new DisposableObserver<T>() {
//                    @Override
//                    public void onNext(T t) {
//                        onCallBack.onSuccess(t);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ComUtil.toast(msg);
//                        onCallBack.onError(msg);
//                        HandleException.getInstance().Handle(e);
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });

//    }


}
