package com.stkj.supermarket.setting.data;

import com.stkj.deviceinterface.model.DeviceHardwareInfo;
import com.tencent.mmkv.MMKV;

/**
 * 收银设置
 */
public class PaymentSettingMMKV {

    public static final String MMKV_NAME = "payment_setting";
    public static final String KEY_OPEN_CASHIER = "open_cashier_pay";
    public static final String KEY_MONEY_MOFEN = "open_money_mofen";
    public static final String KEY_MONEY_MOJIAO = "open_money_mojiao";
    public static final String KEY_CHANGE_ORDER_PRICE = "open_change_order_price";

    public static final String KEY_DEVICE1_TYPE = "device1_type";
    public static final String KEY_DEVICE2_TYPE = "device2_type";
    public static final String KEY_DEVICE3_TYPE = "device3_type";

    public static final String KEY_SWITCH_TONGLIAN_PAY = "switch_tonglian_pay";

    public static boolean isOpenCashierPay() {
        MMKV mmkv = getMMKV();
        return mmkv.getBoolean(KEY_OPEN_CASHIER, false);
    }

    public static void putCashierPay(boolean openCashier) {
        getMMKV().putBoolean(KEY_OPEN_CASHIER, openCashier);
    }

    public static boolean isMoneyMoJiao() {
        MMKV mmkv = getMMKV();
        return mmkv.getBoolean(KEY_MONEY_MOJIAO, false);
    }

    public static void putMoneyMoJiao(boolean openMoJiao) {
        getMMKV().putBoolean(KEY_MONEY_MOJIAO, openMoJiao);
    }

    public static boolean isMoneyMoFen() {
        MMKV mmkv = getMMKV();
        return mmkv.getBoolean(KEY_MONEY_MOFEN, false);
    }

    public static void putMoneyMoFen(boolean openMoFen) {
        getMMKV().putBoolean(KEY_MONEY_MOFEN, openMoFen);
    }

    public static boolean isChangeOrderPrice() {
        MMKV mmkv = getMMKV();
        return mmkv.getBoolean(KEY_CHANGE_ORDER_PRICE, false);
    }

    public static void putChangeOrderPrice(boolean openChangePrice) {
        getMMKV().putBoolean(KEY_CHANGE_ORDER_PRICE, openChangePrice);
    }

    public static int getDevice1Type() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_DEVICE1_TYPE, DeviceHardwareInfo.TYPE_SCAN_GUN_KEYBOARD);
    }

    public static void putDevice1Type(int deviceType) {
        getMMKV().putInt(KEY_DEVICE1_TYPE, deviceType);
    }

    public static int getDevice2Type() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_DEVICE2_TYPE, DeviceHardwareInfo.TYPE_PRINTER);
    }

    public static void putDevice2Type(int deviceType) {
        getMMKV().putInt(KEY_DEVICE2_TYPE, deviceType);
    }

    public static int getDevice3Type() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_DEVICE3_TYPE, DeviceHardwareInfo.TYPE_MONEY_BOX);
    }

    public static void putDevice3Type(int deviceType) {
        getMMKV().putInt(KEY_DEVICE3_TYPE, deviceType);
    }

    public static boolean getSwitchTongLianPay() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_SWITCH_TONGLIAN_PAY, true);
    }

    public static void putSwitchTongLianPay(boolean switchTongLianPay) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_SWITCH_TONGLIAN_PAY, switchTongLianPay);
    }

    public static MMKV getMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
