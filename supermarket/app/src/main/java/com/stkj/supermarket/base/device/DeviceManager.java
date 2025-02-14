package com.stkj.supermarket.base.device;

import android.content.Context;

import com.stkj.common.log.LogHelper;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.supermarket.BuildConfig;

public enum DeviceManager {
    INSTANCE;

    private DeviceInterface mDeviceInterface;

    /**
     * 初始化设备接口(通过反射)
     */
    public void initDevice(Context context) {
        String deviceInterfaceClassName = BuildConfig.deviceInterface;
        try {
            Class<?> aClass = Class.forName(deviceInterfaceClassName);
            mDeviceInterface = (DeviceInterface) aClass.newInstance();
            mDeviceInterface.init(context);
//            mDeviceInterface.showOrHideSysNavBar(false);
            LogHelper.print("--DeviceManager--initDevice success: obj" + mDeviceInterface + " className: " + deviceInterfaceClassName);
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--initDevice error: " + e.getMessage() + " className: " + deviceInterfaceClassName);
            mDeviceInterface = new DefaultDeviceImpl();
        }
    }

    public DeviceInterface getDeviceInterface() {
        return mDeviceInterface;
    }

}