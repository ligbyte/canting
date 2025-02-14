package com.stkj.infocollect.setting.data;

import com.tencent.mmkv.MMKV;

/**
 * 设备设置
 */
public class DeviceSettingMMKV {

    public static final String MMKV_NAME = "device_setting";
    public static final String KEY_SCREEN_PROTECT_NAME = "screen_protect_time";
    public static final String KEY_OPEN_SYS_LOG = "open_sys_log";

    public static void putScreenProtectTime(int interval) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.putInt(KEY_SCREEN_PROTECT_NAME, interval);
    }

    public static int getScreenProtectTime() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeInt(KEY_SCREEN_PROTECT_NAME, -1);
    }

    public static boolean isOpenSysLog() {
        MMKV mmkv = getSettingMMKV();
        return mmkv.getBoolean(KEY_OPEN_SYS_LOG, false);
    }

    public static void putOpenSysLog(boolean openSysLog) {
        getSettingMMKV().putBoolean(KEY_OPEN_SYS_LOG, openSysLog);
    }

    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
