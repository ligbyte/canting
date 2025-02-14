package com.stkj.cashiermini.login.service;

import com.stkj.cashiermini.base.model.BaseResponse;
import com.stkj.cashiermini.login.model.UserInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 登录接口
 */
public interface LoginService {
    @POST("/api/webapp/auth/device/deviceLogin")
    Observable<BaseResponse<UserInfo>> login(@Body Map<String, String> paramsMap);
}
