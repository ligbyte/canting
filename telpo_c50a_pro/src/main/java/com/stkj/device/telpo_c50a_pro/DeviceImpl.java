package com.stkj.device.telpo_c50a_pro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.common.apiutil.multireader.MultiReader;
import com.common.apiutil.system.SystemApiUtil;
import com.common.entity.CardTypeMsg;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.deviceinterface.ScanKeyManager;
import com.stkj.deviceinterface.callback.OnMoneyBoxListener;
import com.stkj.deviceinterface.callback.OnPrintListener;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnReadWeightListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.stkj.deviceinterface.model.DeviceHardwareInfo;
import com.stkj.deviceinterface.model.PrinterData;
import com.szsicod.print.escpos.PrinterAPI;
import com.szsicod.print.io.USBAPI;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import aclasdriver.AclasScale;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 实现天波c50a板子
 */
public class DeviceImpl extends DeviceInterface implements ScanKeyManager.OnScanValueListener {

    static {
        //如果使用UsbNativeAPI 必须加载 适用root板 这个不需要权限窗口和申请权限
        System.loadLibrary("usb1.0");
        //串口
        System.loadLibrary("serial_icod");
        //图片
        System.loadLibrary("image_icod");
    }

    private SystemApiUtil systemApiUtil;
    //扫码串口
    private SerialHelper qrcodeSerialHelper;
    //扫码枪串口
    private SerialHelper scanGunSerialHelper;
    //称重串口
//    private SerialHelper readWeightSerialHelper;
    //os2称重sdk
    private AclasScale aclasScale;
    //读卡串口
    private MultiReader multiReader;
    //打印机设备
    private PrinterAPI printerAPI;
    private ScanKeyManager scanKeyManager;
    private List<DeviceHardwareInfo> mUSBHardwareInfoList = new ArrayList<>();
    private DeviceHardwareInfo printDeviceInfo = new DeviceHardwareInfo(1155, 30016, "打印机", DeviceHardwareInfo.TYPE_PRINTER);
    //键盘模式扫码枪
    private DeviceHardwareInfo keyBoardScanGunDeviceInfo = new DeviceHardwareInfo(7851, 6690, "扫码枪-键盘模式", DeviceHardwareInfo.TYPE_SCAN_GUN_KEYBOARD);
    //串口模式扫码枪
    private DeviceHardwareInfo serialScanGunDeviceInfo = new DeviceHardwareInfo(7851, 6662, "扫码枪-串口模式", DeviceHardwareInfo.TYPE_SCAN_GUN_SERIAL_PORT);

    @Override
    public void init(Context context) {
        systemApiUtil = new SystemApiUtil(context);
        printerAPI = PrinterAPI.getInstance();
        scanKeyManager = new ScanKeyManager(this);
        mUSBHardwareInfoList.add(keyBoardScanGunDeviceInfo);
        mUSBHardwareInfoList.add(serialScanGunDeviceInfo);
        mUSBHardwareInfoList.add(printDeviceInfo);
    }

    @Override
    public void release() {
        super.release();
        hasOpenReadICCard.set(false);
        hasOpenScanQrCode.set(false);
        hasOpenReadWeight.set(false);
        try {
            if (qrcodeSerialHelper != null) {
                qrcodeSerialHelper.close();
                qrcodeSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            qrcodeSerialHelper = null;
        }
//        try {
//            if (readWeightSerialHelper != null) {
//                readWeightSerialHelper.close();
//                readWeightSerialHelper = null;
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            readWeightSerialHelper = null;
//        }
        try {
            if (multiReader != null) {
                multiReader.closeReader();
                multiReader = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            multiReader = null;
        }
        try {
            if (aclasScale != null) {
                aclasScale.StopRead();
                aclasScale.close();
                aclasScale = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            aclasScale = null;
        }
        try {
            printerAPI.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDeviceName() {
        return "天波_C50A_PRO";
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
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    multiReader = new MultiReader(AppManager.INSTANCE.getApplication());
                    boolean openReader = multiReader.openReader(AppManager.INSTANCE.getApplication());
                    LogHelper.print("---readICCard---openReader: " + openReader);
                    if (openReader) {
                        hasOpenReadICCard.set(true);
                        while (hasOpenReadICCard.get()) {
                            CardTypeMsg cardData = multiReader.readUIDType(false);
                            if (cardData != null) {
                                String cardUID = cardData.getCardUID();
                                if (!TextUtils.isEmpty(cardUID)) {
                                    String realCardNumber = ParseData.decodeHexStringIdcard2Int(cardUID);
                                    LogHelper.print("---readICCard---cardUID: " + cardUID + " realCardNumber: " + realCardNumber);
                                    if (!TextUtils.isEmpty(realCardNumber)) {
                                        notifyOnReadCardData(realCardNumber);
                                    }
                                }
                            }
                            Thread.sleep(1000);
                        }
                    } else {
                        notifyOnReadCardError("读卡器初始化失败，请重试");
                        hasOpenReadICCard.set(false);
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
        String sport = "/dev/ttyACM0";
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

    private AtomicBoolean hasOpenReadWeight = new AtomicBoolean();

    @Override
    public void readWeight(OnReadWeightListener onReadWeightListener) {
        registerReadWeightListener(onReadWeightListener);
        if (hasOpenReadWeight.get()) {
            return;
        }
        try {
            aclasScale = new AclasScale(new File("/dev/ttyS4"), 0, new AclasScale.AclasScaleListener() {
                @Override
                public void OnError(int i) {
                    notifyOnReadWeightError("称重设备回调异常,错误码:" + i);
                    hasOpenReadWeight.set(false);
                    LogHelper.print("--DeviceManager--AclasScale--OnError: " + i);

                }

                @Override
                public void OnDataReceive(AclasScale.St_Data stData) {
                    LogHelper.print("--DeviceManager--AclasScale--OnDataReceive: " + stData.m_fWeight + " unit:" + stData.m_strUnit);
                    notifyOnReadWeightData(String.valueOf(stData.m_fWeight), stData.m_strUnit);
                }

                @Override
                public void OnReadTare(float v, boolean b) {
                    LogHelper.print("--DeviceManager--AclasScale--OnReadTare: " + v + " b: " + b);
                }
            });
            aclasScale.open();
            aclasScale.StartRead();
            hasOpenReadWeight.set(true);
        } catch (Throwable e) {
            e.printStackTrace();
            notifyOnReadWeightError("称重设备打开异常");
            hasOpenReadWeight.set(false);
        }
//        String sport = "/dev/ttyS4";
//        try {
//            readWeightSerialHelper = new SerialHelper(sport, 9600) {
//                @Override
//                protected void onDataReceived(ComBean comBean) {
//                    try {
//                        String dataStr = new String(comBean.bRec, StandardCharsets.UTF_8);
//                        LogHelper.print("--DeviceManager--" + sport + "--端口接收原始数据: " + dataStr);
//                        if (!TextUtils.isEmpty(dataStr)) {
//
//                        }
//                        //关闭端口，避免重复刷
////                        close();
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                        notifyOnScanQRCodeError("读取重量数据失败");
//                    }
//                }
//            };
//            readWeightSerialHelper.open();
//            hasOpenReadWeight.set(true);
//            LogHelper.print("----DeviceManager--" + sport + "打开端口成功");
//        } catch (Throwable e) {
//            e.printStackTrace();
//            LogHelper.print("----DeviceManager--" + sport + "打开端口失败");
//            notifyOnScanQRCodeError("端口" + sport + "打开失败");
//            hasOpenReadWeight.set(false);
//        }
    }

    @Override
    public boolean isSupportReadWeight() {
        return true;
    }

    @Override
    public boolean isSupportPrint() {
        return true;
    }

    @Override
    public void print(List<PrinterData> printerDataList, OnPrintListener onPrintListener) {
        registerPrintListener(onPrintListener);
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    int status = printerAPI.getStatus();
                    String statusDescribe = PrinterStatusDescribe.getStatusDescribe(status);
                    if (TextUtils.equals(statusDescribe, "正常")) {
                        for (PrinterData printerData : printerDataList) {
                            //文字大小
                            int fontSize = printerData.getSize();
                            if (fontSize == PrinterData.SIZE_LARGE) {
                                printerAPI.setCharSize(1, 1);
                            } else if (fontSize == PrinterData.SIZE_SMALL) {
                                printerAPI.smallFontSizeSet();
                            } else {
                                printerAPI.setCharSize(0, 0);
                            }
                            //文字对齐
                            int fontAlign = printerData.getAlign();
                            if (fontAlign == PrinterData.ALIGN_CENTER) {
                                printerAPI.setAlignMode(1);
                            } else if (fontAlign == PrinterData.ALIGN_RIGHT) {
                                printerAPI.setAlignMode(2);
                            } else {
                                printerAPI.setAlignMode(0);
                            }
                            //字体加粗
                            if (printerData.isBold()) {
                                printerAPI.setEmphasizedMode(1);
                            } else {
                                printerAPI.setEmphasizedMode(0);
                            }
                            printerAPI.printString(printerData.getText());
                        }
                        printerAPI.printFeed();
                        int result = printerAPI.cutPaper(66, 0);
                        if (result == PrinterAPI.SUCCESS) {
                            notifyOnPrintSuccess();
                        } else {
                            notifyOnPrintError("打印出错了,错误码:" + result);
                        }
                        printerAPI.init();
                    } else {
                        notifyOnPrintError("打印机状态有误: " + statusDescribe);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    notifyOnPrintError(e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isSupportMoneyBox() {
        return true;
    }

    @Override
    public void openMoneyBox(OnMoneyBoxListener onMoneyBoxListener) {
        registerMoneyBoxListener(onMoneyBoxListener);
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    int ret = printerAPI.openCashDrawer(0, 100, 200);
                    if (ret == PrinterAPI.SUCCESS) {
                        notifyOnBoxOpenSuccess();
                    } else {
                        notifyOnBoxOpenError("钱箱打开失败: " + ret);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    notifyOnBoxOpenError(e.getMessage());
                }
            }
        });
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
        return 0;
    }

    @Override
    public int getBackCameraId() {
        return 1;
    }

    @Override
    public int getFrontCameraId() {
        return 0;
    }

    @Override
    public int getCameraDisplayOrientation() {
        return 0;
    }

    @Override
    public int getIRCameraId() {
        return 2;
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
                    if (printDeviceInfo.getVendorId() == usbDevice.getVendorId() && printDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //打印机
                        USBAPI usbapi = new USBAPI(AppManager.INSTANCE.getApplication(), usbDevice);
                        if (printerAPI.connect(usbapi) == PrinterAPI.SUCCESS) {
                            printDeviceInfo.setUsbDevice(usbDevice);
                            notifyAttachDevice(printDeviceInfo);
                            AppToast.toastMsg("打印机连接成功");
                        } else {
                            usbapi.closeDevice();
                        }
                    } else if (keyBoardScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && keyBoardScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
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
                    if (printDeviceInfo.getVendorId() == usbDevice.getVendorId() && printDeviceInfo.getProductId() == usbDevice.getProductId()) {
                        //打印机
                        printerAPI.disconnect();
                        printDeviceInfo.setUsbDevice(null);
                        notifyDetachDevice(printDeviceInfo);
                        AppToast.toastMsg("打印机已断开");
                    } else if (keyBoardScanGunDeviceInfo.getVendorId() == usbDevice.getVendorId() && keyBoardScanGunDeviceInfo.getProductId() == usbDevice.getProductId()) {
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
    private void openSerialScanGun() {
        String sport = "/dev/ttyACM1";
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
    private void closeSerialScanGun() {
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
        return 366;
    }

    @Override
    public int get80cmDetectFaceMinThreshold() {
        return 416;
    }

    @Override
    public int get100cmDetectFaceMinThreshold() {
        return 446;
    }

    @Override
    public void silenceInstallApk(String apkPath) {
        systemApiUtil.installApp(apkPath, AppManager.INSTANCE.getApplication().getPackageName());
    }
}
