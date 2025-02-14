package com.stkj.infocollect.base.tts;

import android.app.Activity;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.stkj.infocollect.base.callback.TTSVoiceListener;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.setting.data.TTSSettingMMKV;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;

import java.util.HashMap;
import java.util.Locale;

/**
 * 语音播放
 * com.google.android.tts 谷歌文字转语音引擎，不支持5.0以下系统
 * com.iflytek.speechcloud 科大讯飞语音引擎3.0，支持4.0以上系统
 * com.iflytek.speechsuite 新版科大讯飞语音引擎，2018年开始新版手机一般会内置，如oppo、vivo、华为
 * com.baidu.duersdk.opensdk 度秘语音引擎3.0 不支持5.0以下系统
 * com.iflytek.tts 科大讯飞语音合成，较老，不支持7.0以上系统
 */
public class TTSVoiceHelper extends ActivityWeakRefHolder {

    public static final String TEST_WORDS = "这是一条语音播报";
    private TextToSpeech ttsVoice;
    private UtteranceProgressListener mUtteranceProgressListener;
    private HashMap<String, TTSVoiceListener> voiceListenerMap;

    public TTSVoiceHelper(@NonNull Activity activity) {
        super(activity);
        voiceListenerMap = new HashMap<>();
        mUtteranceProgressListener = new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                LogHelper.print("--TTSVoiceHelper--utteranceId: onStart: " + utteranceId);
                TTSVoiceListener ttsVoiceListener = voiceListenerMap.get(utteranceId);
                if (ttsVoiceListener != null) {
                    ttsVoiceListener.onVoiceStart();
                }
            }

            @Override
            public void onDone(String utteranceId) {
                LogHelper.print("--TTSVoiceHelper--utteranceId: onDone: " + utteranceId);
                TTSVoiceListener ttsVoiceListener = voiceListenerMap.get(utteranceId);
                if (ttsVoiceListener != null) {
                    ttsVoiceListener.onVoiceEnd();
                }
                voiceListenerMap.remove(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                LogHelper.print("--TTSVoiceHelper--utteranceId: onError: " + utteranceId);
                TTSVoiceListener ttsVoiceListener = voiceListenerMap.get(utteranceId);
                if (ttsVoiceListener != null) {
                    ttsVoiceListener.onVoiceError();
                }
                voiceListenerMap.remove(utteranceId);
            }
        };
    }

    public void setSpeed(float rate) {
        if (ttsVoice != null) {
            ttsVoice.setSpeechRate(rate);
            speakByTTSVoice(TEST_WORDS);
        }
    }

    public void setVoice() {
        if (ttsVoice != null) {
            speakByTTSVoice(TEST_WORDS);
        }
    }

    public void speakByTTSVoice(String words) {
        speakByTTSVoice(words, null, null);
    }

    public void speakByTTSVoice(String words, String voiceId, TTSVoiceListener ttsVoiceListener) {
        speakByTTSVoice(words, null, voiceId, ttsVoiceListener);
    }

    public void speakByTTSVoice(String words, HashMap<String, String> params, String voiceId, TTSVoiceListener ttsVoiceListener) {
        if (TextUtils.isEmpty(words)) {
            return;
        }
//        stopTTSVoice();
        if (ttsVoice != null) {
            try {
//                float speakVoice = TTSSettingMMKV.getTTSSpeakVoice();
                if (params == null) {
                    params = new HashMap<>();
//                    params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(speakVoice));
                    params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
                }
                if (!TextUtils.isEmpty(voiceId) && ttsVoiceListener != null) {
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, voiceId);
                    voiceListenerMap.put(voiceId, ttsVoiceListener);
                }
                ttsVoice.speak(words, TextToSpeech.QUEUE_FLUSH, params);
            } catch (Throwable e) {
                e.printStackTrace();
                AppToast.toastMsg("语音播报失败,请重试");
                releaseTTSVoice();
                if (ttsVoiceListener != null) {
                    ttsVoiceListener.onVoiceError();
                }
            }
        } else {
            initTTSVoice(words);
            AppToast.toastMsg("初始化语音中,请稍等");
        }
    }

    public void initTTSVoice(String words) {
        ttsVoice = new TextToSpeech(AppManager.INSTANCE.getApplication(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                LogHelper.print("TTSVoiceHelper", "initTTSVoice--status: " + status);
                if (status == TextToSpeech.SUCCESS) {
                    ttsVoice.setLanguage(Locale.CHINA);
//                    ttsVoice.setPitch(TTSSettingMMKV.getTTSSpeakVoice());
                    ttsVoice.setSpeechRate(TTSSettingMMKV.getTTSSpeakSpeed());
                    ttsVoice.setOnUtteranceProgressListener(mUtteranceProgressListener);
                    if (!TextUtils.isEmpty(words)) {
                        speakByTTSVoice(words);
                    }
                } else {
                    if (!TextUtils.isEmpty(words)) {
                        Activity activityWithCheck = getHolderActivityWithCheck();
                        if (activityWithCheck != null) {
                            CommonAlertDialogFragment.build()
                                    .setAlertTitleTxt("提示")
                                    .setAlertContentTxt("语音初始化失败,请重试")
                                    .setLeftNavTxt("确定")
                                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                        @Override
                                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                            initTTSVoice(words);
                                        }
                                    })
                                    .setRightNavTxt("取消")
                                    .show(activityWithCheck);
                        }
                    }
                }
            }
        });
    }

    public void stopTTSVoice() {
        if (ttsVoice != null) {
            try {
                ttsVoice.stop();
            } catch (Throwable e) {
                e.printStackTrace();
                releaseTTSVoice();
            }
        }
    }

    private void releaseTTSVoice() {
        if (ttsVoice != null) {
            try {
                ttsVoice.stop();
                ttsVoice.shutdown();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            ttsVoice = null;
            voiceListenerMap.clear();
        }
    }

    @Override
    public void onClear() {
        releaseTTSVoice();
    }
}