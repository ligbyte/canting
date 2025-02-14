package com.stkj.cashiermini;

import android.app.Application;
import android.os.Process;
import android.text.TextUtils;

import com.stkj.cashiermini.base.net.AppNetManager;
import com.stkj.cashiermini.setting.data.ServerSettingMMKV;
import com.stkj.common.constants.AppCommonConstants;
import com.stkj.common.core.AppManager;
import com.stkj.common.crash.XCrashHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.mmkv.MMKVInit;
import com.stkj.common.net.okhttp.OkHttpManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.RxHelper;
import com.stkj.common.utils.ProcessUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.print("---MainApplication----onCreate pid: " + Process.myPid());
        AppCommonConstants.APP_PREFIX_TAG = "superMarketMini";
        AppManager.INSTANCE.init(this);
        if (!ProcessUtils.isMainProcess()) {
            return;
        }
        MMKVInit.init(this);
        RxHelper.init();
        LogHelper.setLogEnable(BuildConfig.DEBUG);
        OkHttpManager.INSTANCE.setHttpLogEnable(BuildConfig.DEBUG);
        OkHttpManager.INSTANCE.setLogSwitch(BuildConfig.DEBUG);
        OkHttpManager.INSTANCE.setDefIntercept(AppNetManager.INSTANCE.getAppOkhttpHttpIntercept());
        RetrofitManager.INSTANCE.setConvertJsonListener(AppNetManager.INSTANCE.getRetrofitJsonConvertListener());
        if (BuildConfig.DEBUG) {
            String serverAddress = ServerSettingMMKV.getServerAddress();
            if (!TextUtils.isEmpty(serverAddress)) {
                RetrofitManager.INSTANCE.setDefaultBaseUrl(serverAddress);
            } else {
                RetrofitManager.INSTANCE.setDefaultBaseUrl(AppNetManager.API_TEST_URL);
            }
        } else {
            RetrofitManager.INSTANCE.setDefaultBaseUrl(AppNetManager.API_OFFICIAL_URL);
        }
        //崩溃日志
        XCrashHelper.init(this);
    }
}
