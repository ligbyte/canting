package com.stkj.cashier.pay.service;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.pay.model.ConsumerRecordListResponse;
import com.stkj.cashier.pay.model.IntervalCardType;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.model.CanteenCurrentTimeInfo;
import com.stkj.cashier.pay.model.TakeMealListResult;
import com.stkj.cashier.pay.model.TakeMealResult;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface PayService {

    //获取消费账单列表
    @GET("home/v3/index")
    Observable<BaseNetResponse<ConsumerRecordListResponse>> getConsumerRecordList(@QueryMap Map<String, String> requestParams);

    //获取退款账单列表
    @GET("home/v3/index")
    Observable<BaseNetResponse<ConsumerRecordListResponse>> getRefundOrderList(@QueryMap Map<String, String> requestParams);

    //去退款账单
    @POST("home/v3/index")
    Observable<BaseNetResponse<Object>> refundOrder(@Body Map<String, String> paramsMap);

    //去支付
    @GET("home/v3/index")
    Observable<BaseNetResponse<ModifyBalanceResult>> goToPay(@QueryMap Map<String, String> requestParams);

    //获取支付状态
    @GET("home/v3/index")
    Observable<BaseNetResponse<ModifyBalanceResult>> getPayStatus(@QueryMap Map<String, String> requestParams);

    //获取取餐列表
    @GET("home/v3/index")
    Observable<TakeMealListResult> takeMealList(@QueryMap Map<String, String> requestParams);

    //获取取餐列表
    @GET("home/v3/index")
    Observable<TakeMealListResult> takeCodeQuery(@QueryMap Map<String, String> requestParams);

    //出餐
    @POST("home/v3/index")
    Observable<BaseNetResponse<TakeMealResult>> takeMeal(@QueryMap Map<String, String> requestParams);

    //获取当前餐厅时段信息
    @GET("home/v3/index")
    Observable<BaseNetResponse<CanteenCurrentTimeInfo>> getCanteenTimeInfo(@QueryMap Map<String, String> requestParams);

    //获取按次消费的金额信息
    @GET("home/v3/index")
    Observable<BaseNetResponse<List<IntervalCardType>>> getIntervalCardType(@QueryMap Map<String, String> requestParams);
}
