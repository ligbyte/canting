package com.stkj.infocollect.setting.service;

import com.stkj.infocollect.base.model.BaseNetResponse;
import com.stkj.infocollect.setting.model.CheckAppVersion;
import com.stkj.infocollect.setting.model.FacePassPeopleListInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SettingService {
    /**
     * 设备录入人员信息接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FacePassPeopleListInfo>> getAllFacePass(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<String>> facePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调(同步)
     */
    @GET("home/v3/index")
    Call<BaseNetResponse<String>> syncFacePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 检查更新
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<CheckAppVersion>> checkAppVersion(@QueryMap Map<String, String> requestParams);


    /**
     * 设备 APP 升级回调接口
     */
    @GET("home/v3/index")
    Call<BaseNetResponse<String>> appUpgradeCallback(@QueryMap Map<String, String> requestParams);

}
