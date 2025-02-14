package com.stkj.cashiermini.order.service;

import com.stkj.cashiermini.base.model.BaseNetResponse;
import com.stkj.cashiermini.order.model.OrderListResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OrderService {

    //快速查找商品列表
    @GET("home/v3/index")
    Observable<BaseNetResponse<OrderListResponse>> getOrderList(@QueryMap Map<String, String> requestParams);


}
