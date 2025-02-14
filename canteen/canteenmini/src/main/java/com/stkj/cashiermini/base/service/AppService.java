package com.stkj.cashiermini.base.service;

import com.stkj.cashiermini.base.model.AppNetInitResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface AppService {

    /**
     * 设备初始化
     */
    @GET("/home/shop/index")
    Observable<AppNetInitResponse> appInit(@QueryMap Map<String, String> queryMap);


}
