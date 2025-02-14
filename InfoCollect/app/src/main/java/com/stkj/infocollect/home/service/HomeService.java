package com.stkj.infocollect.home.service;

import com.stkj.infocollect.base.model.BaseNetResponse;
import com.stkj.infocollect.home.model.HeartBeatInfo;
import com.stkj.infocollect.home.model.OfflineSetInfo;
import com.stkj.infocollect.home.model.StoreInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 首页相关请求接口
 */
public interface HomeService {

    /**
     * 设备查询心跳接口
     */
    @GET("home/v3/heartBeat")
    Observable<BaseNetResponse<HeartBeatInfo>> heartBeat(@QueryMap Map<String, String> params);

    /**
     * 设备录入公司名称接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<StoreInfo>> getStoreInfo(@QueryMap Map<String, String> params);

    /**
     * 设备录入脱机参数接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<OfflineSetInfo>> offlineSet(@QueryMap Map<String, String> params);
}
