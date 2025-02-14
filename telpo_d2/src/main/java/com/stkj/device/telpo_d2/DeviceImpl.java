package com.stkj.device.telpo_d2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.common.api.system.SystemApiUtil;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.deviceinterface.ScanKeyManager;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.deviceinterface.model.DeviceHardwareInfo;
import com.telpo.nfc.NfcUtil;
import com.telpo.nfc.SelectCardReturn;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.schedulers.Schedulers;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 实现天波D2板子
 */
public class DeviceImpl extends DeviceInterface implements ScanKeyManager.OnScanValueListener {

    private SystemApiUtil systemApiUtil;
    //扫码串口
    private SerialHelper qrcodeSerialHelper;
    //扫码枪串口
    private SerialHelper scanGunSerialHelper;
    //读卡串口
    private NfcUtil nfcUtil;
    private List<DeviceHardwareInfo> mUSBHardwareInfoList = new ArrayList<>();
    //键盘模式扫码枪
    private DeviceHardwareInfo keyBoardScanGunDeviceInfo = new DeviceHardwareInfo(7851, 6690, "扫码枪-键盘模式", DeviceHardwareInfo.TYPE_SCAN_GUN_KEYBOARD);
    //串口模式扫码枪
    private DeviceHardwareInfo serialScanGunDeviceInfo = new DeviceHardwareInfo(7851, 6662, "扫码枪-串口模式", DeviceHardwareInfo.TYPE_SCAN_GUN_SERIAL_PORT);
    private ScanKeyManager scanKeyManager;

    @Override
    public void init(Context context) {
        systemApiUtil = new SystemApiUtil(context);
        scanKeyManager = new ScanKeyManager(this);
        mUSBHardwareInfoList.add(keyBoardScanGunDeviceInfo);
        mUSBHardwareInfoList.add(serialScanGunDeviceInfo);
    }

    @Override
    public void release() {
        super.release();
        hasOpenReadICCard.set(false);
        hasOpenScanQrCode.set(false);
        try {
            if (qrcodeSerialHelper != null) {
                qrcodeSerialHelper.close();
                qrcodeSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            qrcodeSerialHelper = null;
        }
        closeSerialScanGun();
        try {
            if (nfcUtil != null) {
                nfcUtil.destroySerial();
                nfcUtil = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            nfcUtil = null;
        }
    }

    @Override
    public String getDeviceName() {
        return "天波_D2";
    }

    @SuppressLint("PrivateApi")
    @Override
    public String getMachineNumber() {
//        //通过反射获取sn号
        String serial = "";
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = String.valueOf(get.invoke(c, "ro.serialno"));
            if (!serial.equals("") && !serial.equals("unknown")) {
                return serial;
            }
            //9.0及以上无法获取到sn，此方法为补充，能够获取到多数高版本手机 sn
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = Build.getSerial();
                if (!TextUtils.isEmpty(serial)) {
                    return serial;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Build.SERIAL;
    }

    private AtomicBoolean hasOpenReadICCard = new AtomicBoolean();

    @Override
    public void readICCard(OnReadICCardListener readCardListener) {
        registerICCardListener(readCardListener);
        if (hasOpenReadICCard.get()) {
            return;
        }
        nfcUtil = NfcUtil.getInstance();
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    nfcUtil.initSerial();
                    hasOpenReadICCard.set(true);
                    while (hasOpenReadICCard.get()) {
                        SelectCardReturn selectCardReturn = nfcUtil.selectCard();
                        if (selectCardReturn != null) {
                            byte[] cardData = selectCardReturn.getCardNum();
                            String realCardNumber = ParseData.decodeHexString(NfcUtil.toHexString(cardData));
                            if (!TextUtils.isEmpty(realCardNumber)) {
                                notifyOnReadCardData(realCardNumber);
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    notifyOnReadCardError(e.getMessage());
                    hasOpenReadICCard.set(false);
                }
            }
        });
    }

    @Override
    public boolean isSupportReadICCard() {
        return true;
    }

    private AtomicBoolean hasOpenScanQrCode = new AtomicBoolean();

    @Override
    public void scanQrCode(OnScanQRCodeListener onScanQRCodeListener) {
        registerScanQRCodeListener(onScanQRCodeListener);
        if (hasOpenScanQrCode.get()) {
            return;
        }
        String sport = "/dev/ttyHSL0";
        try {
            qrcodeSerialHelper = new SerialHelper(sport, 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String dataStr = new String(comBean.bRec, StandardCharsets.UTF_8);
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收原始数据: " + dataStr);
                        String qrcodeData = dataStr.replace("\r", "");
                        if (dataStr.length() >= 4) {
                            notifyOnScanQrCode(qrcodeData);
                        } else {
                            notifyOnScanQRCodeError("扫码数据错误");
                        }
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收卡数据: " + qrcodeData);
                        //关闭端口，避免重复刷
//                        close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnScanQRCodeError("读取卡数据失败");
                    }
                }
            };
            qrcodeSerialHelper.open();
            hasOpenScanQrCode.set(true);
            LogHelper.print("----DeviceManager--" + sport + "打开端口成功");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("----DeviceManager--" + sport + "打开端口失败");
            notifyOnScanQRCodeError("端口" + sport + "打开失败");
            hasOpenScanQrCode.set(false);
        }
    }

    @Override
    public boolean isSupportScanQrCode() {
        return true;
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
    public boolean rebootDevice() {
        systemApiUtil.rebootDevice();
        return true;
    }

    @Override
    public boolean shutDownDevice() {
        systemApiUtil.shutdown();
        return true;
    }

    @Override
    public boolean showOrHideSysStatusBar(boolean showOrHide) {
        if (showOrHide) {
            systemApiUtil.showStatusBar();
        } else {
            systemApiUtil.hideStatusBar();
        }
        return true;
    }

    @Override
    public boolean showOrHideSysNavBar(boolean showOrHide) {
        if (showOrHide) {
            systemApiUtil.showNavigationBar();
        } else {
            systemApiUtil.hideNavigationBar();
        }
        return true;
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
    public int getConsumeLayRes() {
        return 1;
    }

    @Override
    public int getCameraDisplayOrientation() {
        return 0;
    }


    @Override
    public int getIRCameraDisplayOrientation() {
        return 180;
    }

    @Override
    public boolean isSupportUSBDevice() {
        return true;
    }

    @Override
    public void initUsbDevices(HashMap<String, UsbDevice> usbDeviceMap) {
        for (Map.Entry<String, UsbDevice> usbDevice : usbDeviceMap.entrySet()) {
            UsbDevice usbDeviceValue = usbDevice.getValue();
            attachUsbDevice(usbDeviceValue);
        }
    }

    @Override
    public void attachUsbDevice(UsbDevice usbDevice) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    if (keyBoardScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && keyBoardScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //扫码枪-键盘
                        keyBoardScanGunDeviceInfo.setUsbDevice(usbDevice);
                        notifyAttachDevice(keyBoardScanGunDeviceInfo);
                        AppToast.toastMsg("扫码枪连接成功");
                    } else if (serialScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && serialScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //扫码枪-串口
                        serialScanGunDeviceInfo.setUsbDevice(usbDevice);
                        notifyAttachDevice(serialScanGunDeviceInfo);
                        AppToast.toastMsg("扫码枪连接成功");
                        openSerialScanGun();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void detachUsbDevice(UsbDevice usbDevice) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    if (keyBoardScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && keyBoardScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //扫码枪-键盘
                        keyBoardScanGunDeviceInfo.setUsbDevice(null);
                        notifyDetachDevice(keyBoardScanGunDeviceInfo);
                        AppToast.toastMsg("扫码枪已断开");
                    } else if (serialScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && serialScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //扫码枪-串口
                        serialScanGunDeviceInfo.setUsbDevice(null);
                        notifyDetachDevice(serialScanGunDeviceInfo);
                        AppToast.toastMsg("扫码枪已断开");
                        closeSerialScanGun();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打开串口模式的扫码枪
     */
    private void openSerialScanGun(){
        String sport = "/dev/ttyACM0";
        try {
            scanGunSerialHelper = new SerialHelper(sport, 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String dataStr = new String(comBean.bRec, StandardCharsets.UTF_8);
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收原始数据: " + dataStr);
                        String qrcodeData = dataStr.replace("\r", "");
                        if (dataStr.length() >= 4) {
                            notifyOnScanQrCode(qrcodeData);
                        } else {
                            notifyOnScanQRCodeError("扫码数据错误");
                        }
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收卡数据: " + qrcodeData);
                        //关闭端口，避免重复刷
//                        close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnScanQRCodeError("读取卡数据失败");
                    }
                }
            };
            scanGunSerialHelper.open();
            LogHelper.print("----DeviceManager--" + sport + "打开端口成功");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("----DeviceManager--" + sport + "打开端口失败");
            notifyOnScanQRCodeError("端口" + sport + "打开失败");
        }
    }

    /**
     * 关闭串口模式的扫码枪
     */
    private void closeSerialScanGun(){
        try {
            if (scanGunSerialHelper != null) {
                scanGunSerialHelper.close();
                scanGunSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            scanGunSerialHelper = null;
        }
    }

    @Override
    public List<DeviceHardwareInfo> getUSBDeviceHardwareInfoList() {
        return mUSBHardwareInfoList;
    }

    @Override
    public boolean isCanDispatchKeyEvent() {
        UsbDevice usbDevice = keyBoardScanGunDeviceInfo.getUsbDevice();
        if (usbDevice != null) {
            LogHelper.print("--dispatchKeyEvent--isCanDispatchKeyEvent true");
            return true;
        }
        LogHelper.print("--dispatchKeyEvent--isCanDispatchKeyEvent false");
        return false;
    }

    @Override
    public boolean isFinishDispatchKeyEvent() {
        return scanKeyManager.isFinishOnceScan();
    }

    @Override
    public void dispatchKeyEvent(KeyEvent event) {
        if (scanKeyManager != null) {
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                scanKeyManager.setFinishOnceScan(true);
            } else {
                scanKeyManager.setFinishOnceScan(false);
            }
            scanKeyManager.analysisKeyEvent(event);
        }
    }

    @Override
    public void onScanValue(String value) {
        LogHelper.print("---DeviceManager-onScanValue value: " + value);
        notifyOnScanQrCode(value);
    }

    @Override
    public int get50cmDetectFaceMinThreshold() {
        return 220;
    }

    @Override
    public int get80cmDetectFaceMinThreshold() {
        return 300;
    }

    @Override
    public int get100cmDetectFaceMinThreshold() {
        return 340;
    }

    @Override
    public void silenceInstallApk(String apkPath) {
        systemApiUtil.installApp(apkPath, AppManager.INSTANCE.getApplication().getPackageName());
    }
}
