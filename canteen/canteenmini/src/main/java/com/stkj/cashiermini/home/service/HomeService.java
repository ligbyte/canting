package com.stkj.cashiermini.home.service;

import com.stkj.cashiermini.base.model.BaseNetResponse;
import com.stkj.cashiermini.home.model.StoreInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 首页相关请求接口
 */
public interface HomeService {

    /**
     * 设备录入公司名称接口
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<StoreInfo>> getStoreInfo(@QueryMap Map<String, String> params);
}
