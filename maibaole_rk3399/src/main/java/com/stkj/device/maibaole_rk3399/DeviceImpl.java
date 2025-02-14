package com.stkj.device.maibaole_rk3399;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.utils.ConvertUtils;
import com.stkj.device.maibaole_rk3568.Hex2StringUtlis;
import com.stkj.device.maibaole_rk3568.ParseData;
import com.stkj.deviceinterface.DeviceInterface;
import com.stkj.deviceinterface.callback.OnReadICCardListener;
import com.stkj.deviceinterface.callback.OnReadWeightListener;
import com.stkj.deviceinterface.callback.OnScanQRCodeListener;
import com.yf.yf_rk3399_api.YF_RK3399_API_Manager;

import java.util.concurrent.atomic.AtomicBoolean;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 实现迈宝乐rk3399板子
 */
public class DeviceImpl extends DeviceInterface {

    private YF_RK3399_API_Manager apiManager;
    private SerialHelper qrcodeSerialHelper;
    private SerialHelper readCardSerialHelper;
    private SerialHelper readWeightSerialHelper;

    @Override
    public void init(Context context) {
        apiManager = new YF_RK3399_API_Manager(context);
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
        try {
            if (readCardSerialHelper != null) {
                readCardSerialHelper.close();
                readCardSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            readCardSerialHelper = null;
        }
        try {
            if (readWeightSerialHelper != null) {
                readWeightSerialHelper.close();
                readWeightSerialHelper = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            readWeightSerialHelper = null;
        }
    }

    @Override
    public String getDeviceName() {
        return "迈宝乐_rk3399";
    }

    @Override
    public String getMachineNumber() {
        return  Settings.Secure.getString(AppManager.INSTANCE.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
//        return apiManager.yfgetSerialNumber();
    }

    private AtomicBoolean hasOpenReadICCard = new AtomicBoolean();

    @Override
    public void readICCard(OnReadICCardListener readCardListener) {
        registerICCardListener(readCardListener);
        if (hasOpenReadICCard.get()) {
            return;
        }
        String sport = "/dev/ttyS1";
        try {
            readCardSerialHelper = new SerialHelper(sport, 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收原始数据: " + data);
                        if (!data.startsWith("FE02")) {
                            notifyOnReadCardError("读取卡数据错误");
                        } else {
                            String cardData = ParseData.decodeHexString(data.substring(8, 16));
                            LogHelper.print("--DeviceManager--" + sport + "--端口接收卡数据: " + cardData);
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
            LogHelper.print("--DeviceManager--打开端口" + sport + "成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口" + sport + "打开失败: ");
            notifyOnReadCardError("端口" + sport + "打开失败");
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
        String sport = "/dev/ttyS4";
        try {
            qrcodeSerialHelper = new SerialHelper(sport, 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        LogHelper.print("--DeviceManager--" + sport + "--端口接收原始数据: " + data);
                        if (!data.endsWith("0D0A")) {
                            notifyOnScanQRCodeError("扫码数据错误");
                        } else {
                            String scanningCodeReadData = data.substring(0, data.length() - 3);
                            String qrcodeData = Hex2StringUtlis.hexStringToString(scanningCodeReadData);
                            LogHelper.print("--DeviceManager--" + sport + "--端口扫码数据: " + qrcodeData);
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
            LogHelper.print("--DeviceManager--打开端口" + sport + "成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口" + sport + "打开失败: ");
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
        String sport = "/dev/ttyS3";
        try {
            readWeightSerialHelper = new SerialHelper(sport, 9600) {
                @Override
                protected void onDataReceived(ComBean comBean) {
                    try {
                        String data = ConvertUtils.bytes2HexString(comBean.bRec);
                        if (data.contains("2D")) {
                            notifyOnReadWeightData("0.00", "kg");
                            LogHelper.print("--DeviceManager--" + sport + "--端口称重数据0.00");
                        } else {
                            String hexData = Hex2StringUtlis.hexStringToString(data.substring(8, 20));
                            notifyOnReadWeightData(hexData, "kg");
                            LogHelper.print("--DeviceManager--" + sport + "--端口称重数据: " + hexData);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notifyOnReadWeightError("称重数据出错");
                        LogHelper.print("--DeviceManager--" + sport + "--称重数据出错: " + e.getMessage());
                    }
                }
            };
            readWeightSerialHelper.open();
            hasOpenReadWeight.set(true);
            LogHelper.print("--DeviceManager--打开端口" + sport + "成功: ");
        } catch (Throwable e) {
            e.printStackTrace();
            LogHelper.print("--DeviceManager--端口" + sport + "打开失败: ");
            notifyOnReadWeightError("端口" + sport + "打开失败");
            hasOpenReadWeight.set(false);
        }
    }

    @Override
    public boolean isSupportReadWeight() {
        return true;
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
        apiManager.yfReboot();
        return true;
    }

    @Override
    public boolean shutDownDevice() {
        apiManager.yfShutDown();
        return true;
    }

    @Override
    public boolean showOrHideSysStatusBar(boolean showOrHide) {
        if (showOrHide) {
            apiManager.yfsetStatusBarDisplay(true);
        } else {
            apiManager.yfsetStatusBarDisplay(false);
        }
        return true;
    }

    @Override
    public boolean showOrHideSysNavBar(boolean showOrHide) {
        if (showOrHide) {
            apiManager.yfsetNavigationBarVisibility(true);
        } else {
            apiManager.yfsetNavigationBarVisibility(false);
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
        return 2;
    }

    @Override
    public int get50cmDetectFaceMinThreshold() {
        return 330;
    }

    @Override
    public int get80cmDetectFaceMinThreshold() {
        return 400;
    }

    @Override
    public int get100cmDetectFaceMinThreshold() {
        return 432;
    }
}
