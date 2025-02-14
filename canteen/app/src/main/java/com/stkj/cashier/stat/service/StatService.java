package com.stkj.cashier.stat.service;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.stat.model.CanteenSummary;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 餐厅统计相关
 */
public interface StatService {

    //获取餐厅统计
    @GET("home/v3/index")
    Observable<BaseNetResponse<CanteenSummary>> getCanteenSummary(@QueryMap Map<String, String> requestParams);

}
