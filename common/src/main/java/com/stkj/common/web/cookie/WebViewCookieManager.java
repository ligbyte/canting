package com.stkj.common.web.cookie;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;

import java.net.URL;

import com.stkj.common.utils.AndroidUtils;

/**
 * WebView cookie管理
 */
public class WebViewCookieManager {

    /**
     * 同步cookie
     */
    public static void syncCookies(String url) {
        syncCookies(url, false);
    }

    /**
     * 同步cookie
     */
    public static void syncCookies(String url, boolean needRemoveSessionCookie) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http") && !url.startsWith("https")) {
            return;
        }
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (needRemoveSessionCookie) {
                cookieManager.removeSessionCookies(null);
            }
            URL netUrl = new URL(url);
            String host = netUrl.getHost();
            //设置cookie
            cookieManager.setCookie(url, "VersionName=" + AndroidUtils.getAppVersionName() + ";domain=" + host + ";" + "" + "path=/;");
            cookieManager.setCookie(url, "channel=" + AndroidUtils.getMetaDataValue("channel") + ";domain=" + host + ";" + "" + "path=/;");
            cookieManager.flush();
        } catch (Exception e) {
            Log.d("WebViewCookieManager", "--syncCookies error--");
        }
    }

    /**
     * 清理cookie
     */
    public static void clearAllCookies() {
        try {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
