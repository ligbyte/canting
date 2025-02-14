package com.stkj.deviceinterface.callback;

import com.stkj.deviceinterface.model.DeviceHardwareInfo;

public interface DeviceStatusListener {
    void onAttachDevice(DeviceHardwareInfo deviceHardwareInfo);
    void onDetachDevice(DeviceHardwareInfo deviceHardwareInfo);
}
