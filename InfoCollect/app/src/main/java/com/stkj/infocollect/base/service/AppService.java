package com.stkj.infocollect.base.service;

import com.stkj.infocollect.base.model.AppNetInitResponse;
import com.stkj.infocollect.base.model.BaseResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface AppService {

    /**
     * 设备初始化
     */
    @GET("/home/v3/setDomain")
    Observable<AppNetInitResponse> appInit(@QueryMap Map<String, String> queryMap);

    /**
     * 设备查询网络状态接口
     */
    @GET("home/v3/healthCheck")
    Observable<BaseResponse<Object>> netHealthCheck(@QueryMap Map<String, String> params);

    /**
     * 上传文件接口
     */
    @Multipart
    @POST("/api/webapp/drapp/file/uploadMinioReturnUrl")
    Observable<BaseResponse<String>> uploadFile(@Part MultipartBody.Part file);

}
