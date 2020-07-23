package com.gfq.grefreshview.net;

import java.util.Map;

import okhttp3.Interceptor;

/**
 * @created GaoFuq
 * @Date 2020/7/23 18:00
 * @Descaption
 */
public class APIConfig {
    private String baseUrl;
    private Interceptor headerInterceptor;
    private String cacheDir;
    private long cacheSize;

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public Interceptor getHeaderInterceptor() {
        return headerInterceptor;
    }

    public void setHeaderInterceptor(Interceptor headerInterceptor) {
        this.headerInterceptor = headerInterceptor;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public long getCacheSize() {
        return cacheSize;
    }
}
