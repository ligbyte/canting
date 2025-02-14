package com.stkj.supermarketmini.base.net;

import android.text.TextUtils;

import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarketmini.base.callback.AppNetCallback;
import com.stkj.supermarketmini.base.model.AppNetInitResponse;
import com.stkj.supermarketmini.base.model.ShopInitInfo;
import com.stkj.supermarketmini.base.service.AppService;

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

    public void initAppNet(String machineNumber, AppNetCallback appNetCallback) {
        if (isRequestingDeviceDomain) {
            return;
        }
        isRequestingDeviceDomain = true;
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("machine_Number", machineNumber);
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
                                if (appNetCallback != null) {
                                    appNetCallback.onNetInitSuccess(machineNumber);
                                }
                            } else {
                                if (appNetCallback != null) {
                                    appNetCallback.onNetInitError("DeviceDomain为空!");
                                }
                            }
                        } else {
                            if (appNetCallback != null) {
                                appNetCallback.onNetInitError("响应Code: " + response.getCode() + " msg: " + response.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isRequestingDeviceDomain = false;
                        if (appNetCallback != null) {
                            appNetCallback.onNetInitError("error: " + e.getMessage());
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
//        RetrofitManager.INSTANCE.removeAllRetrofit();
    }

    public boolean isRequestingDeviceDomain() {
        return isRequestingDeviceDomain;
    }

}