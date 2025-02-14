package com.stkj.common.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.stkj.common.core.AppManager;

/**
 * 屏幕工具类
 */
public class DisplayUtils {

    /**
     * 获取显示屏幕 根据index
     * @param displayIndex
     */
    public static Display getIndexDisplay(int displayIndex) {
        DisplayManager mDisplayManager = (DisplayManager) AppManager.INSTANCE.getApplication().getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays.length > displayIndex) {
            return displays[displayIndex];
        }
        return null;
    }
}
