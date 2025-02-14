package com.stkj.infocollect.home.helper;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.infocollect.base.model.BaseNetResponse;
import com.stkj.infocollect.base.net.ParamsUtils;
import com.stkj.infocollect.home.model.HeartBeatInfo;
import com.stkj.infocollect.home.model.OfflineSetInfo;
import com.stkj.infocollect.home.service.HomeService;
import com.stkj.infocollect.setting.data.ServerSettingMMKV;
import com.stkj.infocollect.setting.helper.FacePassHelper;
import com.stkj.infocollect.setting.helper.StoreInfoHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import java.util.TreeMap;

/**
 * 心跳帮助类
 */
public class HeartBeatHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    //下发人脸数据
    public static final String COMMAND_UPDATE_FACE_PASS = "1";
    //离线
    public static final String COMMAND_OFFLINE_SET = "3";
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
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .heartBeat(ParamsUtils.newMachineParamsMap())
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
                                        case COMMAND_OFFLINE_SET:
                                            offlineSet();
                                            break;
                                        case COMMAND_UPDATE_STORE_INFO:
                                            Activity activity3 = getHolderActivityWithCheck();
                                            if (activity3 != null) {
                                                StoreInfoHelper storeInfoHelper = ActivityHolderFactory.get(StoreInfoHelper.class, activity3);
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

    public void offlineSet() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        TreeMap<String, String> offlineSetParams = ParamsUtils.newSortParamsMapWithMode("OfflineSet");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(HomeService.class)
                .offlineSet(ParamsUtils.signSortParamsMap(offlineSetParams))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<OfflineSetInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OfflineSetInfo> baseNetResponse) {
                        if (baseNetResponse.getData() != null) {
                            LogHelper.print("--HeartBeatHelper--offlineSet success: " + baseNetResponse.getData());
                        } else {
                            LogHelper.print("--HeartBeatHelper--offlineSet success: data is null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.print("--HeartBeatHelper--offlineSet error: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onClear() {

    }

}
