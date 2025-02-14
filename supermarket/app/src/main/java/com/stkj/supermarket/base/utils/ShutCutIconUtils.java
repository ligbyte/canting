package com.stkj.supermarket.base.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.stkj.supermarket.R;
import com.stkj.supermarket.base.receiver.AppCommonReceiver;
import com.stkj.supermarket.home.ui.activity.MainActivity;

/**
 * 快捷方式工具类
 */
public class ShutCutIconUtils {

    public static void addShortCutCompact(Context context) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            Intent shortcutInfoIntent = new Intent(context, MainActivity.class);
            shortcutInfoIntent.setAction(Intent.ACTION_VIEW); //action必须设置，不然报错

            Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_launcher);
            ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(context, "The only id")
                    .setIcon(IconCompat.createWithBitmap(appIcon))
                    .setShortLabel(context.getApplicationInfo().name)
                    .setIntent(shortcutInfoIntent)
                    .build();

            //当添加快捷方式的确认弹框弹出来时，将被回调
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AppCommonReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            ShortcutManagerCompat.requestPinShortcut(context, info, shortcutCallbackIntent.getIntentSender());
        }
    }

}
