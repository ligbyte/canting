package com.stkj.common.device;

import android.text.TextUtils;
import android.view.InputDevice;

import com.stkj.common.log.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入设备工具类
 */
public class InputDeviceUtils {

    public static boolean checkHasInputDevice(String deviceName) {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice device = InputDevice.getDevice(deviceId);
            if (device != null) {
                String inputDeviceName = device.getName();
                if (TextUtils.equals(inputDeviceName, deviceName)) {
                    LogHelper.print("-InputDeviceUtils-checkHasInputDevice-device:" + device);
                    return true;
                }
            }
        }
        return false;
    }

    public static List<InputDevice> getInputDeviceList() {
        int[] deviceIds = InputDevice.getDeviceIds();
        List<InputDevice> inputDeviceList = new ArrayList<>();
        for (int deviceId : deviceIds) {
            InputDevice device = InputDevice.getDevice(deviceId);
            if (device != null) {
                LogHelper.print("-InputDeviceUtils-getInputDeviceList-device:" + device);
                inputDeviceList.add(device);
            }
        }
        return inputDeviceList;
    }

}
