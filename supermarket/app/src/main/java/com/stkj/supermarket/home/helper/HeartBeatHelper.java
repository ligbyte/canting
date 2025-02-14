package com.stkj.supermarket.home.helper;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseNetResponse;
import com.stkj.supermarket.base.net.ParamsUtils;
import com.stkj.supermarket.home.model.HeartBeatInfo;
import com.stkj.supermarket.home.service.HomeService;
import com.stkj.supermarket.setting.data.ServerSettingMMKV;
import com.stkj.supermarket.setting.helper.FacePassHelper;
import com.stkj.supermarket.setting.helper.StoreInfoHelper;

import java.util.TreeMap;

/**
 * 心跳帮助类
 */
public class HeartBeatHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    //下发人脸数据
    public static final String COMMAND_UPDATE_FACE_PASS = "1";
    //商店信息
    public static final String COMMAND_UPDATE_STORE_INFO = "4";

    private int currentTotalBeatSecond;
    private int mServerBeatDelayTime;//默认30秒
    private boolean forbidHeatBeat;

    public HeartBeatHelper(@NonNull Activity activity) {
        super(activity);
        mServerBeatDelayTime = ServerSettingMMKV.getHeartBeatInterval();
        currentTotalBeatSecond = 0;
    }

    public void setForbidHeatBeat(boolean forbidHeatBeat) {
        this.forbidHeatBeat = forbidHeatBeat;
    }

    public void setServerBeatDelay(int mBeatDelay) {
        this.mServerBeatDelayTime = mBeatDelay;
        currentTotalBeatSecond = 0;
    }

    @Override
    public void onCountDown() {
        currentTotalBeatSecond += 1;
        if (currentTotalBeatSecond >= mServerBeatDelayTime) {
            LogHelper.print("---HeartBeatHelper---requestHeartBeat");
            requestHeartBeat();
            currentTotalBeatSecond = 0;
        }
    }

    public void requestHeartBeat() {
        if (forbidHeatBeat) {
            return;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("mode", "ReportDeviceStatus");
        paramsMap.put("machine_Number", DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .heartBeat(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<HeartBeatInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<HeartBeatInfo> baseNetResponse) {
                        if (forbidHeatBeat) {
                            return;
                        }
                        HeartBeatInfo heartBeatInfo = baseNetResponse.getData();
                        if (heartBeatInfo != null) {
                            String updateUserInfo = heartBeatInfo.getUpdateUserInfo();
                            if (!TextUtils.isEmpty(updateUserInfo)) {
                                String[] split = updateUserInfo.split("&");
                                for (String command : split) {
                                    switch (command) {
                                        case COMMAND_UPDATE_FACE_PASS:
                                            Activity activity = getHolderActivityWithCheck();
                                            if (activity != null) {
                                                FacePassHelper facePassHelper = ActivityHolderFactory.get(FacePassHelper.class, activity);
                                                if (facePassHelper != null) {
                                                    facePassHelper.requestFacePass(1, false);
                                                }
                                            }
                                            break;
                                        case COMMAND_UPDATE_STORE_INFO:
                                            Activity withCheck = getHolderActivityWithCheck();
                                            if (withCheck != null) {
                                                StoreInfoHelper storeInfoHelper = ActivityHolderFactory.get(StoreInfoHelper.class, withCheck);
                                                if (storeInfoHelper != null) {
                                                    storeInfoHelper.requestStoreInfo();
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onClear() {

    }

}
