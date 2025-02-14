package com.stkj.cashiermini.setting.data;

import android.content.Context;
import android.text.TextUtils;

import com.stkj.cashiermini.BuildConfig;
import com.stkj.cashiermini.base.net.AppNetManager;
import com.stkj.cashiermini.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashiermini.base.utils.CommonDialogUtils;
import com.tencent.mmkv.MMKV;

/**
 * 服务器地址设置
 */
public class ServerSettingMMKV {

    public static final String MMKV_NAME = "server_setting";
    public static final String KEY_SERVER_NAME = "server_address";

    public static void putServerAddress(String serverAddress) {
        MMKV serverSettingMMKV = getServerSettingMMKV();
        serverSettingMMKV.putString(KEY_SERVER_NAME, serverAddress);
    }

    public static String getServerAddress() {
        MMKV serverSettingMMKV = getServerSettingMMKV();
        return serverSettingMMKV.decodeString(KEY_SERVER_NAME, "");
    }

    public static MMKV getServerSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }

    /**
     * 处理修改服务器地址逻辑
     */
    public static void handleChangeServerAddress(Context context, String address) {
        if (TextUtils.isEmpty(address)) {
            CommonDialogUtils.showTipsDialog(context, "服务器地址不能为空");
            return;
        }
        //判断测试正式内部
        if (BuildConfig.DEBUG) {
            if (address.contains("测试")) {
                address = AppNetManager.API_TEST_URL;
            } else if (address.contains("正式")) {
                address = AppNetManager.API_OFFICIAL_URL;
            } else if (address.contains("内部")) {
                address = "http://10.10.10.108:9003";
            }
        }
        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }
        final String finalAddress = address;
        CommonDialogUtils.showAppResetDialog(context, "修改服务器地址会清理本地数据,确定吗?", new CommonAlertDialogFragment.OnSweetClickListener() {
            @Override
            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                putServerAddress(finalAddress);
            }
        });
    }
}
