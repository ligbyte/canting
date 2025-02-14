package com.stkj.supermarket.base.data;

import com.tencent.mmkv.MMKV;

public class AppCommonMMKV {

    public static final String MMKV_NAME = "app_common";

    public static final String KEY_HAS_ADD_SHUT_CUT = "has_add_shut_cut";


    public static void putHasAddShutCut(boolean hasAssShutCut) {
        MMKV settingMMKV = getSettingMMKV();
        settingMMKV.putBoolean(KEY_HAS_ADD_SHUT_CUT, hasAssShutCut);
    }

    public static boolean getHasAddShutCut() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeBool(KEY_HAS_ADD_SHUT_CUT, false);
    }

    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }

}
