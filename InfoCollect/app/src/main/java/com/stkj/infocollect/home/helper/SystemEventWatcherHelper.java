package com.stkj.infocollect.home.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.base.net.ParamsUtils;
import com.stkj.infocollect.base.service.AppService;
import com.stkj.infocollect.base.tts.TTSVoiceHelper;
import com.stkj.infocollect.setting.data.ServerSettingMMKV;
import com.stkj.infocollect.setting.data.TTSSettingMMKV;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统事件广播监听
 */
public class SystemEventWatcherHelper extends ActivityWeakRefHolder implements CountDownHelper.OnCountDownListener {

    private BroadcastReceiver mSysEventReceiver;
    private Set<OnSystemEventListener> dateListenerSet = new HashSet<>();
//    private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
//        @Override
//        public void onAvailable(@NonNull Network network) {
//            LogHelper.print("--SystemEventWatcherHelper---onAvailable: " + network);
//            runUIThreadWithCheck(new Runnable() {
//                @Override
//                public void run() {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onNetworkAvailable();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onLost(@NonNull Network network) {
//            LogHelper.print("--SystemEventWatcherHelper---onLost last network: " + network);
//            runUIThreadWithCheck(new Runnable() {
//                @Override
//                public void run() {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onNetworkLost();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onUnavailable() {
//            LogHelper.print("--SystemEventWatcherHelper---onUnavailable");
//            runUIThreadWithCheck(new Runnable() {
//                @Override
//                public void run() {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onNetworkUnavailable();
//                    }
//                }
//            });
//        }
//    };

    public SystemEventWatcherHelper(@NonNull Activity activity) {
        super(activity);
//        ConnectivityManager connectivityManager = activity.getSystemService(ConnectivityManager.class);
//        connectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        mSysEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    return;
                }
                String action = intent.getAction();
                if (action == null || action.isEmpty()) {
                    return;
                }

                if (action.equals(Intent.ACTION_TIME_TICK)) {
                    for (OnSystemEventListener listener : dateListenerSet) {
                        listener.onDateTick();
                    }
                    LogHelper.print("--SystemEventWatcherHelper---onDateTick");
                } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                    for (OnSystemEventListener listener : dateListenerSet) {
                        listener.onDateChange();
                    }
                    LogHelper.print("--SystemEventWatcherHelper---onDateChange");
                } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);///电池剩余电量
                    int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);///获取电池满电量数值
                    float batteryPercent = 100;
                    if (batteryLevel != 0 && batteryScale != 0) {
                        batteryPercent = batteryLevel * 100 / (float) batteryScale;
                    }
                    int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);///获取电池状态
                    boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                            batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
                    for (OnSystemEventListener listener : dateListenerSet) {
                        listener.onBatteryChange(batteryPercent, isCharging);
                    }
                    LogHelper.print("--SystemEventWatcherHelper---onBatteryChange batteryLevel = " + batteryLevel + " batteryScale = " + batteryScale + " batteryStatus = " + batteryStatus + " batteryPct = " + batteryPercent);
                }
//                else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onScreenOff();
//                    }
//                    LogHelper.print("--SystemEventWatcherHelper---onScreenOff");
//                } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onScreenOn();
//                    }
//                    LogHelper.print("--SystemEventWatcherHelper---onScreenOn");
//                } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onUserPresent();
//                    }
//                    LogHelper.print("--SystemEventWatcherHelper---onUserPresent");
//                }
//                else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
//                    int level = NetworkUtils.getWifiNetworkRSSILevel(4);
//                    for (OnSystemEventListener listener : dateListenerSet) {
//                        listener.onNetworkRssiChange(level);
//                    }
//                    LogHelper.print("SystemEventWatcherHelper", "onNetworkRssiChange: level: " + level);
//                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        //系统时间变化
        //system every 1 min send broadcast
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        //system hand change time send broadcast
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
//        //亮屏
//        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//        //息屏
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        //解锁
//        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        //电池电量变化
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        activity.registerReceiver(mSysEventReceiver, intentFilter);
//        //wifi信号强度监听
//        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
//        boolean supportMobileSignal = DeviceManager.INSTANCE.getDeviceInterface().isSupportMobileSignal();
//        if (supportMobileSignal) {
//            //移动网络信号监听
//            TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//            mTelephonyManager.listen(new PhoneStateListener() {
//
//                @Override
//                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                    super.onSignalStrengthsChanged(signalStrength);
//                    int level = signalStrength.getLevel();
//                    runUIThreadWithCheck(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (OnSystemEventListener listener : dateListenerSet) {
//                                listener.onNetworkRssiChange(level);
//                            }
//                        }
//                    });
//                    LogHelper.print("SystemEventWatcherHelper", "onNetworkRssiChange: level: " + level);
//                }
//            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//        }
    }

    public void addSystemEventListener(OnSystemEventListener systemDateListener) {
        dateListenerSet.add(systemDateListener);
    }

    public void removeSystemEventListener(OnSystemEventListener systemDateListener) {
        dateListenerSet.remove(systemDateListener);
    }

    @Override
    public void onClear() {
        Activity holderActivity = getHolderActivity();
        if (holderActivity != null) {
            holderActivity.unregisterReceiver(mSysEventReceiver);
//            ConnectivityManager connectivityManager = holderActivity.getSystemService(ConnectivityManager.class);
//            connectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    private int mHealthCheckDelayTime = 5;
    private int currentTotalHealthCheckSecond;
    private boolean isNetworkLost;
    private boolean forbidHealthCheck;

    public void setForbidHealthCheck(boolean forbidHealthCheck) {
        this.forbidHealthCheck = forbidHealthCheck;
    }

    /**
     * 网络状况检查
     */
    private void requestNetHealthCheck() {
        if (forbidHealthCheck) {
            return;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        final long requestTime = System.currentTimeMillis();
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(AppService.class)
                .netHealthCheck(ParamsUtils.newMachineParamsMap())
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<Object>>() {
                    @Override
                    protected void onSuccess(BaseResponse<Object> objectBaseResponse) {
                        long timeInterval = System.currentTimeMillis() - requestTime;
                        //<=300ms   满格
                        //>300ms <= 1s   2格
                        //>1s <= 10s   1格
                        //>10s
                        int level = 0;
                        if (isNetworkLost) {
                            //语音提醒
                            boolean ttsNetStatus = TTSSettingMMKV.getTTSNetStatus();
                            if (ttsNetStatus) {
                                TTSVoiceHelper ttsVoiceHelper = getTTSVoiceHelper();
                                if (ttsVoiceHelper != null) {
                                    ttsVoiceHelper.speakByTTSVoice("网络连接成功");
                                }
                            }
                            isNetworkLost = false;
                            for (OnSystemEventListener listener : dateListenerSet) {
                                listener.onNetworkAvailable();
                            }
                        }
                        if (timeInterval <= 300) {
                            level = 3;
                            for (OnSystemEventListener listener : dateListenerSet) {
                                listener.onNetworkRssiChange(level, timeInterval);
                            }
                        } else if (timeInterval < 1000) {
                            level = 2;
                            for (OnSystemEventListener listener : dateListenerSet) {
                                listener.onNetworkRssiChange(level, timeInterval);
                            }
                        } else if (timeInterval < 10 * 1000) {
                            level = 1;
                            for (OnSystemEventListener listener : dateListenerSet) {
                                listener.onNetworkRssiChange(level, timeInterval);
                            }
                        } else {
                            level = 0;
                            for (OnSystemEventListener listener : dateListenerSet) {
                                listener.onNetworkRssiChange(level, timeInterval);
                            }
                        }
                        LogHelper.print("--requestNetHealthCheck--onSuccess timeInterval =  " + timeInterval + " level =  " + level);
                    }

                    @Override
                    public void onError(Throwable e) {
                        long timeInterval = System.currentTimeMillis() - requestTime;
                        for (OnSystemEventListener listener : dateListenerSet) {
                            listener.onNetworkLost();
                        }
                        if (!isNetworkLost) {
                            //语音提醒
                            boolean ttsNetStatus = TTSSettingMMKV.getTTSNetStatus();
                            if (ttsNetStatus) {
                                TTSVoiceHelper ttsVoiceHelper = getTTSVoiceHelper();
                                if (ttsVoiceHelper != null) {
                                    ttsVoiceHelper.speakByTTSVoice("网络已断开");
                                }
                            }
                        }
                        isNetworkLost = true;
                        LogHelper.print("--requestNetHealthCheck--onError " + timeInterval);
                    }
                });
    }

    @Override
    public void onCountDown() {
        boolean switchHealthCheck = ServerSettingMMKV.getSwitchHealthCheck();
        if (switchHealthCheck) {
            currentTotalHealthCheckSecond += 1;
            if (currentTotalHealthCheckSecond >= mHealthCheckDelayTime) {
                requestNetHealthCheck();
                currentTotalHealthCheckSecond = 0;
            }
        }
    }

    public interface OnSystemEventListener {
//        default void onScreenOff() {
//
//        }
//
//        default void onScreenOn() {
//
//        }
//
//        default void onUserPresent() {
//
//        }

        default void onDateTick() {
        }

        default void onDateChange() {
        }

        default void onNetworkAvailable() {

        }

        default void onNetworkLost() {

        }

        default void onNetworkUnavailable() {

        }

        /**
         * 信号强度(0-4)
         */
        default void onNetworkRssiChange(int level, long delayTime) {

        }

        default void onBatteryChange(float batteryPercent, boolean isChanging) {

        }
    }

    private TTSVoiceHelper getTTSVoiceHelper() {
        return ActivityHolderFactory.get(TTSVoiceHelper.class, getHolderActivityWithCheck());
    }

    public static String getNetworkLevelTips(int level) {
        if (level == 0) {
            //>10s
            return "网络状况很差";
        } else if (level == 1) {
            //>1s <= 10s   1格
            return "网络状况一般";
        } else if (level == 2) {
            //>300ms <= 1s   2格
            return "网络状况良好";
        } else if (level == 3) {
            //<=300ms   满格
            return "网络状况很好";
        }
        return "";
    }

}
