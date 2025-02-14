package com.stkj.supermarketmini.base.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.stkj.common.utils.IntentUtils;

/**
 * 通知栏工具类
 */
public class NotificationUtil {
    public static final String UPDATE_CHANNEL_ID = "notification_channel_update";

    /**
     * 创建channelId
     */
    public static void createUpdateChannelId(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "升级通知";
            String description = "app版本升级通知";
            //无声音，不弹出，在状态栏显示
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(UPDATE_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 创建并检测update通知是否打开
     * 适配8.0
     */
    public static boolean createAndCheckUpdateNotificationEnabled(Context context) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            //8.0以下
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return true;
            } else {
                CharSequence name = "升级通知";
                String description = "app版本升级通知";
                String channelId = UPDATE_CHANNEL_ID;
                //无声音，不弹出，在状态栏显示
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    NotificationChannel updateChannel = notificationManager.getNotificationChannel(channelId);
                    return updateChannel != null && !(updateChannel.getImportance() == NotificationManager.IMPORTANCE_NONE);
                }
            }
        }
        return false;
    }

    /**
     * 打开设置通知权限页面
     *
     * @param context
     */
    public static void jumpNotificationSetting(Context context) {
        try {
            Intent notificationIntent = IntentUtils.getNotificationIntent(context);
            context.startActivity(notificationIntent);
        } catch (Exception e) {
            try {
                // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                Intent intent = IntentUtils.getLaunchAppDetailsSettingsIntent(context.getPackageName(), true);
                context.startActivity(intent);
            } catch (Exception ex) {

            }
        }
    }

}
