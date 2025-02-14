package com.stkj.supermarket.base.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.stkj.common.core.AppManager;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.AndroidUtils;

/**
 * app自启动、安装apk
 */
public class AppCommonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (TextUtils.equals(Intent.ACTION_BOOT_COMPLETED, intentAction)) {
            //判断app是否存在
            Activity homeActivity = AppManager.INSTANCE.getMainActivity();
            if (!ActivityUtils.isActivityFinished(homeActivity)) {
                return;
            }
            //启动app
            AndroidUtils.launchApp();
        }
    }
}
