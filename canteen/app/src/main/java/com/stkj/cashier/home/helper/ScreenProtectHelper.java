package com.stkj.cashier.home.helper;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.stkj.cashier.login.helper.LoginHelper;
import com.stkj.cashier.setting.data.DeviceSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;

/**
 * 屏幕保护监听
 */
public class ScreenProtectHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    private OnScreenProtectListener onScreenProtectListener;
    private int mScreenProtectTime = -1;
    private int currentTotalSecond = 0;
    private boolean stopScreenProtect;
    private boolean forbidScreenProtect;

    public ScreenProtectHelper(@NonNull Activity activity) {
        super(activity);
        mScreenProtectTime = DeviceSettingMMKV.getScreenProtectTime();
    }

    public void setForbidScreenProtect(boolean forbidScreenProtect) {
        this.forbidScreenProtect = forbidScreenProtect;
    }

    public void setOnScreenProtectListener(OnScreenProtectListener onScreenProtectListener) {
        this.onScreenProtectListener = onScreenProtectListener;
    }

    public void setScreenProtectTime(int screenProtectTime) {
        this.mScreenProtectTime = screenProtectTime;
        currentTotalSecond = 0;
    }

    public void stopScreenProtect() {
        stopScreenProtect = true;
        currentTotalSecond = 0;
    }

    public void startScreenProtect() {
        stopScreenProtect = false;
    }

    @Override
    public void onCountDown() {
        if (stopScreenProtect) {
            return;
        }
        if (mScreenProtectTime == -1) {
            return;
        }
        currentTotalSecond += 1;
        if (currentTotalSecond >= mScreenProtectTime) {
            LogHelper.print("--ScreenProtectHelper--onNeedScreenProtect");
            if (!forbidScreenProtect) {
                if (onScreenProtectListener != null) {
                    onScreenProtectListener.onNeedScreenProtect();
                }
            }
            currentTotalSecond = 0;
            stopScreenProtect = true;
        }
    }

    @Override
    public void onClear() {
    }

    public interface OnScreenProtectListener {
        void onNeedScreenProtect();
    }
}
