package com.stkj.cashier.base.device;

import android.content.Context;
import android.util.Log;

import com.stkj.cashier.BuildConfig;
import com.stkj.deviceinterface.DeviceInterface;

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
            Log.i("DeviceManager", "----initDevice success: obj" + mDeviceInterface + " className: " + deviceInterfaceClassName);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.i("DeviceManager", "--DeviceManager--initDevice error: " + e.getMessage() + " className: " + deviceInterfaceClassName);
            mDeviceInterface = new DefaultDeviceImpl();
        }
    }

    public DeviceInterface getDeviceInterface() {
        return mDeviceInterface;
    }

}