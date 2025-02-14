package com.stkj.cashier.setting.data;

import com.stkj.cashier.base.utils.JacksonUtils;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.LocalConsumerRecord;
import com.stkj.deviceinterface.model.DeviceHardwareInfo;
import com.tencent.mmkv.MMKV;

/**
 * 消费设置
 */
public class PaymentSettingMMKV {

    public static final String MMKV_NAME = "payment_setting";

    public static final String KEY_DEVICE1_TYPE = "device1_type";
    public static final String KEY_DEVICE2_TYPE = "device2_type";
    public static final String KEY_DEVICE3_TYPE = "device3_type";
    public static final String KEY_LAST_CONSUMER_RECORD = "last_consumer_record";
    public static final String KEY_CONSUMER_MODE = "consumer_mode";
    public static final String KEY_UNIT_WEIGHT_PRICE = "unit_weight_price";
    public static final String KEY_PAY_SUCCESS_DELAY = "pay_success_delay";
    public static final String KEY_AUTO_WEIGHT_COUNT = "auto_weight_count";
    public static final String KEY_AUTO_WEIGHT_CANCEL_PAY = "auto_weight_cancel_pay";
    public static final String KEY_TAKE_MEAL_QUEUE_LIMIT = "take_meal_queue_limit";
    public static final String KEY_WEIGHT_ATTACH_AMOUNT = "weight_attach_amount";
    public static final String KEY_SWITCH_CONSUMER_CONFIRM = "switch_consumer_confirm";
    public static final String KEY_SWITCH_PAY_TYPE_CARD = "switch_payType_card";
    public static final String KEY_SWITCH_PAY_TYPE_FACE = "switch_payType_face";
    public static final String KEY_SWITCH_PAY_TYPE_SCAN = "switch_payType_scan";
    public static final String KEY_SWITCH_TONGLIAN_PAY = "switch_tonglian_pay";

    public static void putSwitchPayTypeCard(boolean switchPayTypeCard) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_SWITCH_PAY_TYPE_CARD, switchPayTypeCard);
    }

    public static boolean getSwitchPayTypeCard() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_SWITCH_PAY_TYPE_CARD, true);
    }

    public static void putSwitchPayTypeFace(boolean switchPayTypeFace) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_SWITCH_PAY_TYPE_FACE, switchPayTypeFace);
    }

    public static boolean getSwitchPayTypeFace() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_SWITCH_PAY_TYPE_FACE, true);
    }

    public static void putSwitchPayTypeScan(boolean switchPayTypeFace) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_SWITCH_PAY_TYPE_SCAN, switchPayTypeFace);
    }

    public static boolean getSwitchPayTypeScan() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_SWITCH_PAY_TYPE_SCAN, true);
    }

    public static void putSwitchConsumerConfirm(boolean switchConsumerConfirm) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_SWITCH_CONSUMER_CONFIRM, switchConsumerConfirm);
    }

    public static boolean getSwitchConsumerConfirm() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_SWITCH_CONSUMER_CONFIRM, true);
    }

    public static void putSwitchWeightAutoCancelPay(boolean autoWeightCancelPay) {
        MMKV mmkv = getMMKV();
        mmkv.putBoolean(KEY_AUTO_WEIGHT_CANCEL_PAY, autoWeightCancelPay);
    }

    public static boolean getSwitchWeightAutoCancelPay() {
        MMKV mmkv = getMMKV();
        return mmkv.decodeBool(KEY_AUTO_WEIGHT_CANCEL_PAY, false);
    }

    public static int getConsumerMode() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_CONSUMER_MODE, PayConstants.CONSUMER_AMOUNT_MODE);
    }

    public static void putConsumerMode(int consumerMode) {
        getMMKV().putInt(KEY_CONSUMER_MODE, consumerMode);
    }

    public static String getUnitWeightPrice() {
        MMKV mmkv = getMMKV();
        return mmkv.getString(KEY_UNIT_WEIGHT_PRICE, "0.00");
    }

    public static void putUnitWeightPrice(String unitWeightPrice) {
        getMMKV().putString(KEY_UNIT_WEIGHT_PRICE, unitWeightPrice);
    }

    public static String getWeightAttachAmount() {
        MMKV mmkv = getMMKV();
        return mmkv.getString(KEY_WEIGHT_ATTACH_AMOUNT, "0");
    }

    public static void putWeightAttachAmount(String weightAttachAmount) {
        getMMKV().putString(KEY_WEIGHT_ATTACH_AMOUNT, weightAttachAmount);
    }

    public static int getPaySuccessDelay() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_PAY_SUCCESS_DELAY, 2000);
    }

    public static String getPaySuccessDelayString() {
        int paySuccessDelay = getPaySuccessDelay();
        if (paySuccessDelay == 1000) {
            return "1秒";
        } else if (paySuccessDelay == 1500) {
            return "1.5秒";
        } else if (paySuccessDelay == 2000) {
            return "2秒";
        } else if (paySuccessDelay == 2500) {
            return "2.5秒";
        } else if (paySuccessDelay == 3000) {
            return "3秒";
        }
        return "1.5秒";
    }

    public static void putPaySuccessDelay(int i) {
        getMMKV().putInt(KEY_PAY_SUCCESS_DELAY, i);
    }

    public static int getAutoWeightCount() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_AUTO_WEIGHT_COUNT, 10);
    }

    public static void putAutoWeightCount(int i) {
        getMMKV().putInt(KEY_AUTO_WEIGHT_COUNT, i);
    }

    public static int getTakeMealQueueLimit() {
        MMKV mmkv = getMMKV();
        return mmkv.getInt(KEY_TAKE_MEAL_QUEUE_LIMIT, 3);
    }

    public static void putTakeMealQueueLimit(int i) {
        getMMKV().putInt(KEY_TAKE_MEAL_QUEUE_LIMIT, i);
    }

    public static LocalConsumerRecord getLastConsumerRecord() {
        MMKV mmkv = getMMKV();
        String string = mmkv.getString(KEY_LAST_CONSUMER_RECORD, "");
        return JacksonUtils.convertJsonObject(string, LocalConsumerRecord.class);
    }

    public static void putLastConsumerRecord(LocalConsumerRecord localConsumerRecord) {
        MMKV mmkv = getMMKV();
        String jsonString = JacksonUtils.convertJsonString(localConsumerRecord);
        mmkv.putString(KEY_LAST_CONSUMER_RECORD, jsonString);
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
