package com.stkj.supermarket.setting.data;

import com.tencent.mmkv.MMKV;

/**
 * 语音设置
 */
public class TTSSettingMMKV {

    public static final String MMKV_NAME = "tts_setting";

    //    public static final String KEY_SPEAK_VOICE_NAME = "tts_speak_voice";
    public static final String KEY_SPEAK_SPEED_NAME = "tts_speak_speed";
    public static final String KEY_NET_STATUS_NAME = "tts_net_status";

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

    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
