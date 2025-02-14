package com.stkj.supermarketmini.payment.service;

import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.payment.model.AddOrderRequest;
import com.stkj.supermarketmini.payment.model.AddOrderResult;
import com.stkj.supermarketmini.payment.model.ConsumeOrderRequest;
import com.stkj.supermarketmini.payment.model.ConsumeOrderResult;
import com.stkj.supermarketmini.payment.model.OrderHistoryListResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PayService {
    //添加订单
    @POST("/api/webapp/service/sporderinfo/add")
    Observable<BaseResponse<AddOrderResult>> addOrder(@Body AddOrderRequest requestParams);

    //支付订单
    @POST("/api/webapp/service/sporderinfo/consume")
    Observable<BaseResponse<ConsumeOrderResult>> consumeOrder(@Body ConsumeOrderRequest requestParams);

    //订单状态查询
    @GET("/api/webapp/service/sporderinfo/orderStatus")
    Observable<BaseResponse<String>> orderStatus(@Query("payNo") String requestParams);

    //历史订单查询
    @GET("/api/webapp/service/sporderinfo/page")
    Observable<BaseResponse<OrderHistoryListResponse>> getOrderHistoryList(@QueryMap Map<String, Object> requestParams);
}
