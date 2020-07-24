package com.gfq.grefreshview;

import com.gfq.grefreshview.net.APIConfig;
import com.gfq.grefreshview.net.RefreshViewAPIService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @created GaoFuq
 * @Date 2020/7/24 18:00
 * @Descaption
 */
public class Test {
    private void xx(){

        APIConfig apiConfig = new APIConfig();
        apiConfig.setHeaderInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = new Request.Builder().addHeader("token","").build();
                return chain.proceed(request);
            }
        });
        RefreshViewAPIService.getInstance().init(apiConfig);
    }
}
