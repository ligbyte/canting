package com.stkj.supermarket.setting.service;

import com.stkj.supermarket.base.model.BaseNetResponse;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.setting.model.CheckAppVersion;
import com.stkj.supermarket.setting.model.FacePassPeopleInfo;
import com.stkj.supermarket.setting.model.FacePassPeopleListInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SettingService {
    /**
     * 设备录入人员信息接口
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<FacePassPeopleListInfo>> getAllFacePass(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<String>> facePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调(同步)
     */
    @GET("home/shop/index")
    Call<BaseNetResponse<String>> syncFacePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 检查更新
     */
    @GET("home/shop/index")
    Observable<BaseNetResponse<CheckAppVersion>> checkAppVersion(@QueryMap Map<String, String> requestParams);


    /**
     * 设备 APP 升级回调接口
     */
    @GET("home/shop/index")
    Call<BaseNetResponse<String>> appUpgradeCallback(@QueryMap Map<String, String> requestParams);

}
