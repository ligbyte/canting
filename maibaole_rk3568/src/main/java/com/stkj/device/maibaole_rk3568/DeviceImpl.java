package com.stkj.device.maibaole_rk3568;

import android.content.Context;
import android.os.Build;

import com.innohi.YNHAPI;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.ConvertUtils;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;

import java.util.concurrent.atomic.AtomicBoolean;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 实现迈宝乐rk3568板子
 */
public class DeviceImpl extends DeviceInterface {

    private SerialHelper qrcodeSerialHelper;
    private SerialHelper readCardSerialHelper;

    @Override
    public void init(Context context) {

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
        return "迈宝乐_rk3568";
    }

    @Override
    public String getMachineNumber() {
        return Build.SERIAL;
    }

    private AtomicBoolean hasOpenReadICCard = new AtomicBoolean();

    @Override
    public void readICCard(OnReadICCardListener readCardListener) {
        registerICCardListener(readCardListener);
        if (hasOpenReadICCard.get()) {
            return;
        }
        try {
            readCardSerialHelper = new SerialHelper("/dev/ttyS5", 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        LogHelper.print("--DeviceManager--/dev/ttyS5--端口接收原始数据: " + data);
                        if (!data.startsWith("FE02")) {
                            notifyOnReadCardError("读取卡数据错误");
                        } else {
                            String cardData = ParseData.decodeHexString(data.substring(8, 16));
                            LogHelper.print("--DeviceManager--/dev/ttyS5--端口接收卡数据: " + cardData);
                            notifyOnReadCardData(cardData);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnReadCardError("读取卡数据失败");
                    }
                    //蜂鸣声
                    String sound = "FE02021B080000";
                    sendHex(sound);
//                    close();
                }
            };
            readCardSerialHelper.open();
            hasOpenReadICCard.set(true);
            LogHelper.print("--DeviceManager--打开端口/dev/ttyS5成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口/dev/ttyS5打开失败: ");
            notifyOnReadCardError("端口/dev/ttyS5打开失败");
            hasOpenReadICCard.set(false);
        }
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
        try {
            qrcodeSerialHelper = new SerialHelper("/dev/ttyS3", 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        LogHelper.print("--DeviceManager--/dev/ttyS3--端口接收原始数据: " + data);
                        if (!data.endsWith("0D0A")) {
                            notifyOnScanQRCodeError("扫码数据错误");
                        } else {
                            String scanningCodeReadData = data.substring(0, data.length() - 3);
                            String qrcodeData = Hex2StringUtlis.hexStringToString(scanningCodeReadData);
                            LogHelper.print("--DeviceManager--/dev/ttyS3--端口扫码数据: " + qrcodeData);
                            notifyOnScanQrCode(qrcodeData);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnScanQRCodeError("扫码数据失败");
                    }
                    //蜂鸣声
                    String sound = "FE02021B080000";
                    sendHex(sound);
//                    close();
                }
            };
            qrcodeSerialHelper.open();
            hasOpenScanQrCode.set(true);
            LogHelper.print("--DeviceManager--打开端口/dev/ttyS3成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口/dev/ttyS3打开失败: ");
            notifyOnScanQRCodeError("端口/dev/ttyS3打开失败");
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
        YNHAPI.getInstance().reboot();
        return true;
    }

    @Override
    public boolean shutDownDevice() {
        YNHAPI.getInstance().shutdown();
        return true;
    }

    @Override
    public boolean showOrHideSysStatusBar(boolean showOrHide) {
        if (showOrHide) {
            YNHAPI.getInstance().setExtendStatusBarVisibility(YNHAPI.ExtendStatusBarVisibility.VISIBLE);
        } else {
            YNHAPI.getInstance().setExtendStatusBarVisibility(YNHAPI.ExtendStatusBarVisibility.INVISIBLE);
        }
        return true;
    }

    @Override
    public boolean showOrHideSysNavBar(boolean showOrHide) {
        if (showOrHide) {
            YNHAPI.getInstance().setNavigationBarVisibility(YNHAPI.NavigationBarVisibility.VISIBLE);
        } else {
            YNHAPI.getInstance().setNavigationBarVisibility(YNHAPI.NavigationBarVisibility.INVISIBLE);
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
    public int get50cmDetectFaceMinThreshold() {
        return 406;
    }

    @Override
    public int get80cmDetectFaceMinThreshold() {
        return 437;
    }

    @Override
    public int get100cmDetectFaceMinThreshold() {
        return 450;
    }
}
