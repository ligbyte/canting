package com.stkj.common.mmkv;

import com.tencent.mmkv.MMKV;

public class DefaultMMKV {

    public static MMKV getMMKV() {
        return MMKV.defaultMMKV();
    }
}
