package com.stkj.infocollect.card.service;

import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.card.model.CardSetInfo;
import com.stkj.infocollect.card.model.UserCardInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CardService {

    /**
     * 开卡申请
     */
    @POST("/api/webapp/pad/user/addOpen")
    Observable<BaseResponse<String>> submitOpenCardInfo(@Body Map<String, Object> params);

    /**
     * 押金、开卡费
     */
    @GET("/api/webapp/pad/card/getCardSetByTenantId")
    Observable<BaseResponse<CardSetInfo>> getCardSetInfo();

    /**
     * 获取用户信息
     */
    @GET("/api/webapp/pad/card/getCustomerById")
    Observable<BaseResponse<UserCardInfo>> getCustomerById(@Query("customerId") String customerId);

    /**
     * 挂失申请
     */
    @FormUrlEncoded
    @POST("/api/webapp/pad/card/lossCard")
    Observable<BaseResponse<Object>> lossCardApply(@Field("customerId") String customerId);

    /**
     * 挂失补卡申请
     */
    @FormUrlEncoded
    @POST("/api/webapp/pad/card/lossReplaceCard")
    Observable<BaseResponse<Object>> lossReplaceCardApply(@Field("customerId") String customerId);

    /**
     * 取消挂失补卡申请
     */
    @FormUrlEncoded
    @POST("/api/webapp/pad/card/cancelLossCard")
    Observable<BaseResponse<Object>> cancelLossCardApply(@Field("customerId") String customerId);

    /**
     * 检查取消挂失补卡申请记录
     */
    @GET("/api/webapp/pad/card/checkCancelLossCard")
    Observable<BaseResponse<String>> checkCancelLossCardApply(@Query("customerId") String customerId);

}
