package com.stkj.infocollect.setting.data;

import com.tencent.mmkv.MMKV;

/**
 * 语音设置
 */
public class TTSSettingMMKV {

    public static final String MMKV_NAME = "tts_setting";

    //    public static final String KEY_SPEAK_VOICE_NAME = "tts_speak_voice";
    public static final String KEY_SPEAK_SPEED_NAME = "tts_speak_speed";
    public static final String KEY_NET_STATUS_NAME = "tts_net_status";
    public static final String KEY_CONSUME_SUCCESS_VOICE = "consume_success_voice";
    public static final String KEY_TAKE_SUCCESS_VOICE = "take_success_voice";
    public static final String KEY_PAY_TYPE_VOICE = "pay_type_voice";

//    public static void putTTSSpeakVoice(float voice) {
//        MMKV serverSettingMMKV = getSettingMMKV();
//        serverSettingMMKV.putFloat(KEY_SPEAK_VOICE_NAME, voice);
//    }
//
//    public static float getTTSSpeakVoice() {
//        MMKV serverSettingMMKV = getSettingMMKV();
//        return serverSettingMMKV.decodeFloat(KEY_SPEAK_VOICE_NAME, 1.0f);
//    }

    public static void putTTSSpeakSpeed(float speed) {
        MMKV ttsSettingMMKV = getSettingMMKV();
        ttsSettingMMKV.putFloat(KEY_SPEAK_SPEED_NAME, speed);
    }

    public static float getTTSSpeakSpeed() {
        MMKV ttsSettingMMKV = getSettingMMKV();
        return ttsSettingMMKV.decodeFloat(KEY_SPEAK_SPEED_NAME, 0.5f);
    }

    public static void putTTSNetStatus(boolean netStatus) {
        MMKV ttsSettingMMKV = getSettingMMKV();
        ttsSettingMMKV.putBoolean(KEY_NET_STATUS_NAME, netStatus);
    }

    public static boolean getTTSNetStatus() {
        MMKV ttsSettingMMKV = getSettingMMKV();
        return ttsSettingMMKV.decodeBool(KEY_NET_STATUS_NAME, true);
    }

    public static void putConsumeSuccessVoice(String consumeSuccessVoice) {
        MMKV ttsSettingMMKV = getSettingMMKV();
        ttsSettingMMKV.putString(KEY_CONSUME_SUCCESS_VOICE, consumeSuccessVoice);
    }

    public static String getConsumeSuccessVoice() {
        MMKV ttsSettingMMKV = getSettingMMKV();
        return ttsSettingMMKV.getString(KEY_CONSUME_SUCCESS_VOICE, "消费成功");
    }

    public static void putTakeSuccessVoice(String consumeSuccessVoice) {
        MMKV ttsSettingMMKV = getSettingMMKV();
        ttsSettingMMKV.putString(KEY_TAKE_SUCCESS_VOICE, consumeSuccessVoice);
    }

    public static String getTakeSuccessVoice() {
        MMKV ttsSettingMMKV = getSettingMMKV();
        return ttsSettingMMKV.getString(KEY_TAKE_SUCCESS_VOICE, "取餐成功");
    }

    public static void putPayTypeVoice(String payTypeVoice) {
        MMKV ttsSettingMMKV = getSettingMMKV();
        ttsSettingMMKV.putString(KEY_PAY_TYPE_VOICE, payTypeVoice);
    }

    public static String getPayTypeVoice() {
        MMKV ttsSettingMMKV = getSettingMMKV();
        return ttsSettingMMKV.getString(KEY_PAY_TYPE_VOICE, "请刷脸、刷卡或扫码");
    }

    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
