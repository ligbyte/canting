package com.stkj.supermarket.base.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarket.base.callback.AppNetCallback;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.AppNetInitResponse;
import com.stkj.supermarket.base.model.ShopInitInfo;
import com.stkj.supermarket.base.service.AppService;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * app 网络管理类
 */
public enum AppNetManager {
    INSTANCE;
    public static final String API_TEST_URL = "http://101.43.252.67:9003";
    public static final String API_OFFICIAL_URL = "http://101.42.54.44:9003";
    private AppOkhttpIntercept appOkhttpIntercept;
    private AppRetrofitJsonConvertListener retrofitJsonConvertListener;
    private ShopInitInfo mShopInitInfo;
    private boolean isRequestingDeviceDomain;
    private Set<AppNetCallback> netCallbackSet = new HashSet<>();

    public AppOkhttpIntercept getAppOkhttpHttpIntercept() {
        if (appOkhttpIntercept == null) {
            appOkhttpIntercept = new AppOkhttpIntercept();
        }
        return appOkhttpIntercept;
    }

    public AppRetrofitJsonConvertListener getRetrofitJsonConvertListener() {
        if (retrofitJsonConvertListener == null) {
            retrofitJsonConvertListener = new AppRetrofitJsonConvertListener();
        }
        return retrofitJsonConvertListener;
    }

    public void initAppNet() {
        if (isRequestingDeviceDomain) {
            return;
        }
        isRequestingDeviceDomain = true;
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("machine_Number", DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        paramsMap.put("mode", "DeviceDomain");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(AppService.class)
                .appInit(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<AppNetInitResponse>() {
                    @Override
                    protected void onSuccess(AppNetInitResponse response) {
                        isRequestingDeviceDomain = false;
                        LogHelper.print("AppNetManager -- initAppNet --success: " + response.toString());
                        if (response.isSuccess()) {
                            ShopInitInfo shopInitInfo = response.getData();
                            if (shopInitInfo != null && !TextUtils.isEmpty(shopInitInfo.getDomain())) {
                                mShopInitInfo = shopInitInfo;
                                for (AppNetCallback callback : netCallbackSet) {
                                    callback.onNetInitSuccess();
                                }
                            } else {
                                for (AppNetCallback callback : netCallbackSet) {
                                    callback.onNetInitError("DeviceDomain为空!");
                                }
                            }
                        } else {
                            for (AppNetCallback callback : netCallbackSet) {
                                callback.onNetInitError("响应Code: " + response.getCode() + " msg: " + response.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isRequestingDeviceDomain = false;
                        for (AppNetCallback callback : netCallbackSet) {
                            callback.onNetInitError("error: " + e.getMessage());
                        }
                        LogHelper.print("AppNetManager -- initAppNet --error: " + e.getMessage());
                    }
                });
    }

    public String getDeviceDomain() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDomain();
    }

    public String getShopId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getShopId();
    }

    public String getDeviceId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDeviceId();
    }

    public void clearAppNetCache() {
        mShopInitInfo = null;
        isRequestingDeviceDomain = false;
        RetrofitManager.INSTANCE.removeAllRetrofit();
    }

    public boolean isRequestingDeviceDomain() {
        return isRequestingDeviceDomain;
    }

    public void addNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.add(appNetCallback);
    }

    public void removeNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.remove(appNetCallback);
    }
}