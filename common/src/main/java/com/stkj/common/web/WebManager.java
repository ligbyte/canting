package com.stkj.common.web;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebStorage;

import com.stkj.common.web.activity.CommonWebActivity;
import com.stkj.common.web.cookie.WebViewCookieManager;

public enum WebManager {
    INSTANCE;

    //------- 界面管理 ---------

    public Intent getCommonWebIntent(Context context, String webUrl) {
        return getCommonWebIntent(context, webUrl, true);
    }

    public Intent getCommonWebIntent(Context context, String webUrl, boolean isX5WebView) {
        Intent intent = new Intent(context, CommonWebActivity.class);
        intent.putExtra(CommonWebActivity.EXTRA_WEB_URL, webUrl);
        intent.putExtra(CommonWebActivity.EXTRA_WEB_X5, isX5WebView);
        return intent;
    }

    public void clearWebCookies() {
        WebViewCookieManager.clearAllCookies();
    }

    public void clearLocalStorage() {
        WebStorage.getInstance().deleteAllData();
    }

}