package com.stkj.infocollect.home.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.IntentUtils;
import com.stkj.common.utils.NetworkUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.BuildConfig;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.infocollect.home.helper.SystemEventWatcherHelper;
import com.stkj.infocollect.setting.data.ServerSettingMMKV;
import com.stkj.infocollect.setting.helper.StoreInfoHelper;

/**
 * 首页顶部title
 */
public class HomeTitleLayout extends FrameLayout implements SystemEventWatcherHelper.OnSystemEventListener {

    private ImageView ivLogoIcon;
    private ImageView ivSysWifi;
    private TextView tvSysTime;
    private ProgressBar pbBattery;
    private ImageView ivBatteryBg;
    private Drawable batteryDefaultPro;
    private Drawable batteryChargingPro;

    public HomeTitleLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_home_title_lay, this);
        ivLogoIcon = (ImageView) findViewById(R.id.iv_canteen_logo);
        ivSysWifi = (ImageView) findViewById(R.id.iv_sys_wifi);
        tvSysTime = (TextView) findViewById(R.id.tv_sys_time);
        ivSysWifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.INSTANCE.getApplication().startActivity(IntentUtils.getToNetworkSetting());
            }
        });
        ivLogoIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String buildInfo = "编译时间: " + BuildConfig.BUILD_TIME + "\n"
                        + "编译id: " + BuildConfig.GIT_SHA + "\n"
                        + "编译类型: " + (BuildConfig.DEBUG ? "测试版" : "正式版") + "\n"
                        + "版本号: " + BuildConfig.VERSION_NAME + "\n"
                        + "人脸授权: " + (CBGFacePassHandlerHelper.hasFacePassSDKAuth() ? "已授权" : "未授权");
                CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("版本信息")
                        .setAlertContentTxt(buildInfo);
                if (BuildConfig.DEBUG) {
                    commonAlertDialogFragment.setLeftNavTxt("切换服务器")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    showInputServerAddressDialog();
                                }
                            })
                            .setRightNavTxt("确定")
                            .show(context);
                } else {
                    commonAlertDialogFragment.setLeftNavTxt("关闭App")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    AndroidUtils.killApp((Activity) context);
                                }
                            })
                            .setRightNavTxt("确定")
                            .show(context);
                }
                return true;
            }
        });
        pbBattery = (ProgressBar) findViewById(R.id.pb_battery);
        ivBatteryBg = (ImageView) findViewById(R.id.iv_battery_bg);
        batteryDefaultPro = getResources().getDrawable(com.stkj.infocollect.R.drawable.battery_pro_bar_default);
        batteryChargingPro = getResources().getDrawable(com.stkj.infocollect.R.drawable.battery_pro_bar_charging);
        refreshDate();
        refreshWifiLay();
    }

    /**
     * 显示修改服务器地址
     */
    private void showInputServerAddressDialog() {
        CommonInputDialogFragment.build()
                .setTitle("修改服务器地址")
                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        ServerSettingMMKV.handleChangeServerAddress(getContext(), input);
                    }
                }).show(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            SystemEventWatcherHelper systemEventWatcherHelper = ActivityHolderFactory.get(SystemEventWatcherHelper.class, mainActivity);
            if (systemEventWatcherHelper != null) {
                systemEventWatcherHelper.removeSystemEventListener(this);
            }
        }
    }

    private void refreshDate() {
        try {
            String formatDate = DateFormat.format("MM-dd HH:mm", System.currentTimeMillis()).toString();
            tvSysTime.setText(formatDate);
            LogHelper.print("---HomeTitleLayout----refreshDate: " + formatDate);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateTick() {
        refreshDate();
    }

    @Override
    public void onDateChange() {
        refreshDate();
    }

    @Override
    public void onNetworkAvailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkAvailable");
        //获取当wifi信号强度
        refreshWifiLay();
    }

    @Override
    public void onNetworkLost() {
        LogHelper.print("--HomeTitleLayout--onNetworkLost");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
    }

    @Override
    public void onNetworkUnavailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkUnavailable");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
    }

    @Override
    public void onNetworkRssiChange(int level, long delayTime) {
        LogHelper.print("--HomeTitleLayout--onNetworkRssiChange level: " + level);
        switch (level) {
            case 0:
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level0);
                break;
            case 1:
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level1);
                break;
            case 2:
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level2);
                break;
            case 3:
            default:
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level3);
                break;
        }
    }

    @Override
    public void onBatteryChange(float batteryPercent, boolean isChanging) {
        if (isChanging) {
            ivBatteryBg.setBackgroundResource(R.mipmap.icon_battery_charging);
            pbBattery.setProgressDrawable(batteryChargingPro);
        } else {
            ivBatteryBg.setBackgroundResource(R.mipmap.icon_battery_default);
            pbBattery.setProgressDrawable(batteryDefaultPro);
        }
        pbBattery.setProgress((int) batteryPercent);
    }

    private void refreshWifiLay() {
        if (NetworkUtils.isConnected()) {
            onNetworkRssiChange(4, 0);
        } else {
            //无网络
            onNetworkUnavailable();
        }
    }
}
