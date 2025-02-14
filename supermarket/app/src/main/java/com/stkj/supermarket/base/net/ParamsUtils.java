package com.stkj.supermarket.base.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.stkj.common.log.LogHelper;
import com.stkj.supermarket.base.utils.EncryptUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ParamsUtils {

    public static final String DES_PUBLIC_KEY = "St-tech@2023";

    /**
     * 新建请求排序的参数Map
     */
    public static TreeMap<String, String> newSortParamsMap() {
        return new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static Map<String, String> signSortParamsMap(@NonNull TreeMap<String, String> newParamsMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : newParamsMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (!"sign".equals(key) && !"mode".equals(key) && !"consumption_type".equals(key) && !TextUtils.isEmpty(val)) {
                stringBuilder.append(val).append("&");
            }
        }
        String builderString = stringBuilder.toString();
        if (builderString.length() >= 2) {
            String signParamsValue = builderString.substring(0, builderString.length() - 1);
            LogHelper.print("signSortParamsMap sign = " + signParamsValue);
            newParamsMap.put("sign", EncryptUtils.encryptMD5ToString16(signParamsValue));
        }
        return newParamsMap;
    }
}
