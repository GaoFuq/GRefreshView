package com.gfq.grefreshview.net;



import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * retrofit 使用
 * ·@Path：所有在网址中的参数（URL的问号前面），如：http://102.10.10.132/api/Accounts/{accountId}
 * <p>
 * ·Query：URL问号后面的参数，如：http://102.10.10.132/api/Comments?access_token={access_token}
 * <p>
 * ·QueryMap：相当于多个@Query
 * <p>
 * ·Field：用于POST请求，提交单个数据
 * <p>
 * ·Body：相当于多个@Field，以对象的形式提交
 */

public interface RefreshViewAPI {

//   @POST(BASE_URL + "/api/company/getCompanyUserCollect")
//   Observable<API<List<FavoriteMe>>> getCompanyUserCollect(@Body Map<Object, Object> map);

}

