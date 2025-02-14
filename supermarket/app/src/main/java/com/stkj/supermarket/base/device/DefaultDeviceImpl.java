package com.stkj.supermarket.base.device;

import android.content.Context;

import com.stkj.deviceinterface.DeviceInterface;

/**
 * 默认设备接口实现
 */
public class DefaultDeviceImpl extends DeviceInterface {

    @Override
    public boolean isSupportMobileSignal() {
        return true;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public String getDeviceName() {
        return "默认设备";
    }

    @Override
    public String getMachineNumber() {
        return "shopandroidtest1";
    }

    @Override
    public boolean isSupportReadICCard() {
        return false;
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
    public boolean isSupportDualCamera() {
        return false;
    }

}
