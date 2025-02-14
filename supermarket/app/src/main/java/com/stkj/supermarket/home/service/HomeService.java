package com.stkj.supermarket.home.service;

import com.stkj.supermarket.base.model.BaseNetResponse;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.home.model.HeartBeatInfo;
import com.stkj.supermarket.home.model.HomeMenuList;
import com.stkj.supermarket.home.model.StoreInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 首页相关请求接口
 */
public interface HomeService {
    /**
     * 查询首页左侧菜单
     */
    @GET("/api/webapp/drapp/menu/list")
    Observable<BaseResponse<List<HomeMenuList>>> menuList();

    /**
     * 设备查询心跳接口
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<HeartBeatInfo>> heartBeat(@QueryMap Map<String, String> params);

    /**
     * 设备录入公司名称接口
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<StoreInfo>> getStoreInfo(@QueryMap Map<String, String> params);
}
