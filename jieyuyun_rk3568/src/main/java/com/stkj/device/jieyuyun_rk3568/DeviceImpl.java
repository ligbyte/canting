package com.stkj.device.jieyuyun_rk3568;

import android.content.Context;
import android.kh.KaihuangManager;
import android.provider.Settings;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.utils.ConvertUtils;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.deviceinterface.callback.OnReadICCardListener;

import java.util.concurrent.atomic.AtomicBoolean;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 实现捷誉云rk3568板子
 */
public class DeviceImpl extends DeviceInterface {

    private @Nullable KaihuangManager kaihuangManager;
    private SerialHelper readCardSerialHelper;

    @Override
    public void init(Context context) {
        kaihuangManager = (KaihuangManager) context.getSystemService("kaihuang");
    }

    @Override
    public void release() {
        super.release();
        hasOpenReadICCard.set(false);
        try {
            if (readCardSerialHelper != null) {
                readCardSerialHelper.close();
                readCardSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            readCardSerialHelper = null;
        }
    }

    @Override
    public String getDeviceName() {
        return "捷誉云_rk3568";
    }

    @Override
    public String getMachineNumber() {
        return Settings.Secure.getString(AppManager.INSTANCE.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private AtomicBoolean hasOpenReadICCard = new AtomicBoolean();

    @Override
    public void readICCard(OnReadICCardListener readCardListener) {
        registerICCardListener(readCardListener);
        if (hasOpenReadICCard.get()) {
            return;
        }
        try {
            readCardSerialHelper = new SerialHelper("/dev/ttyS4", 115200) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        LogHelper.print("--DeviceManager--/dev/ttyS4--端口接收原始数据: " + data);
                        String cardData = ParseData.decodeHexStringIdcard2Int(data.substring(6, 14));
                        LogHelper.print("--DeviceManager--/dev/ttyS4--端口接收卡数据: " + cardData);
                        notifyOnReadCardData(cardData);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnReadCardError("读取卡数据失败");
                    }
                    //蜂鸣声
//                    SoundPoolHelper.INSTANCE.playSound1();
                }
            };
            readCardSerialHelper.open();
            hasOpenReadICCard.set(true);
            LogHelper.print("--DeviceManager--打开端口/dev/ttyS4成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口/dev/ttyS4打开失败: ");
            notifyOnReadCardError("端口/dev/ttyS4打开失败");
            hasOpenReadICCard.set(false);
        }
    }

    @Override
    public boolean isSupportReadICCard() {
        return true;
    }

    @Override
    public boolean isSupportScanQrCode() {
        return false;
    }

    @Override
    public boolean isSupportReadWeight() {
        return false;
    }

    @Override
    public boolean isSupportPrint() {
        return false;
    }

    @Override
    public boolean isSupportMoneyBox() {
        return false;
    }

    @Override
    public boolean showOrHideSysStatusBar(boolean showOrHide) {
        if (kaihuangManager != null) {
            kaihuangManager.setStatusBar(showOrHide);
            return true;
        }
        return false;
    }

    @Override
    public boolean showOrHideSysNavBar(boolean showOrHide) {
        if (kaihuangManager != null) {
            kaihuangManager.setNavitionBar(showOrHide);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupportDualCamera() {
        return false;
    }

    @Override
    public boolean isSupportMobileSignal() {
        return true;
    }

    @Override
    public void silenceInstallApk(String apkPath) {
        if (kaihuangManager != null) {
            kaihuangManager.installSlientApk(apkPath);
        }
//        AndroidUtils.silenceInstallApk(AppManager.INSTANCE.getApplication(), apkPath, getLaunchActivity());
    }

}
