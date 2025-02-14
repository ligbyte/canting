package com.stkj.infocollect.login.service;

import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.login.model.UserInfo;

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
