package com.stkj.supermarket.setting.helper;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.download.DownloadFileCallback;
import com.stkj.common.download.DownloadFileHelper;
import com.stkj.common.download.DownloadFileInfo;
import com.stkj.common.net.callback.DefaultRetrofitCallback;
import com.stkj.common.net.okhttp.OkHttpManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.permissions.request.WriteStoragePermissionRequest;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.supermarket.BuildConfig;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.model.BaseNetResponse;
import com.stkj.supermarket.base.net.ParamsUtils;
import com.stkj.supermarket.base.permission.AppPermissionHelper;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.utils.NotificationUtil;
import com.stkj.supermarket.setting.model.CheckAppVersion;
import com.stkj.supermarket.setting.service.SettingService;

import java.util.TreeMap;

/**
 * app升级
 */
public class AppUpgradeHelper extends ActivityWeakRefHolder {

    private boolean isCheckUpgrade;
    private OnAppUpgradeListener onAppUpgradeListener;
    private CommonAlertDialogFragment upgradeDialog;
    private static final int NOTIFY_ID = 999;

    public AppUpgradeHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void setOnAppUpgradeListener(OnAppUpgradeListener onAppUpgradeListener) {
        this.onAppUpgradeListener = onAppUpgradeListener;
    }

    public void checkAppVersion() {
        if (isCheckUpgrade) {
            return;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (onAppUpgradeListener != null) {
            onAppUpgradeListener.onCheckVersionStart();
        }
        isCheckUpgrade = true;
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("machine_Number", DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        paramsMap.put("mode", "CheckAppVersion");
        paramsMap.put("deviceType", BuildConfig.deviceType);
        paramsMap.put("version_No", String.valueOf(BuildConfig.VERSION_CODE));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .checkAppVersion(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseNetResponse<CheckAppVersion>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<CheckAppVersion> baseNetResponse) {
                        CheckAppVersion data = baseNetResponse.getData();
                        if (baseNetResponse.isSuccess() && data != null && !TextUtils.isEmpty(data.getUrl())) {
                            handleCheckAppVersion(data);
                        } else {
                            isCheckUpgrade = false;
                            if (onAppUpgradeListener != null) {
                                onAppUpgradeListener.onCheckVersionEnd(baseNetResponse.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isCheckUpgrade = false;
                        if (onAppUpgradeListener != null) {
                            onAppUpgradeListener.onCheckVersionError(e.getMessage());
                        }
                    }
                });
    }

    /**
     * 处理app升级
     */
    private void handleCheckAppVersion(CheckAppVersion appNewVersion) {
        try {
            int newVersion = Integer.parseInt(appNewVersion.getVersion());
            boolean needForceUpgrade = TextUtils.equals("1", appNewVersion.getVersionForce());
            if (newVersion > BuildConfig.VERSION_CODE) {
                Activity activityWithCheck = getHolderActivityWithCheck();
                if (activityWithCheck != null) {
                    upgradeDialog = CommonAlertDialogFragment.build()
                            .setNeedHandleDismiss(true)
                            .setAlertTitleTxt("版本提示")
                            .setAlertContentTxt(appNewVersion.getContent())
                            .setLeftNavTxt("升级")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    if (!needForceUpgrade) {
                                        AppToast.toastMsg("已在后台下载");
                                        if (upgradeDialog != null) {
                                            upgradeDialog.dismiss();
                                        }
                                    }
                                    downloadAppNewVersion(appNewVersion.getUrl(), needForceUpgrade);
                                }
                            });
                    upgradeDialog.setOnDismissListener(new BaseDialogFragment.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            upgradeDialog = null;
                        }
                    });
                    if (needForceUpgrade) {
                        upgradeDialog.show(activityWithCheck);
                    } else {
                        upgradeDialog.setRightNavTxt("取消")
                                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                    @Override
                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                        if (upgradeDialog != null) {
                                            upgradeDialog.dismiss();
                                        }
                                        isCheckUpgrade = false;
                                        if (onAppUpgradeListener != null) {
                                            onAppUpgradeListener.onCheckVersionEnd(null);
                                        }
                                    }
                                }).show(activityWithCheck);
                    }
                } else {
                    isCheckUpgrade = false;
                    if (onAppUpgradeListener != null) {
                        onAppUpgradeListener.onCheckVersionEnd(null);
                    }
                }
            } else {
                isCheckUpgrade = false;
                if (onAppUpgradeListener != null) {
                    onAppUpgradeListener.onNoVersionUpgrade();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            isCheckUpgrade = false;
            if (onAppUpgradeListener != null) {
                onAppUpgradeListener.onCheckVersionEnd(e.getMessage());
            }
        }
    }

    private void downloadAppNewVersion(String downloadUrl, boolean needForceUpgrade) {
        if (!needForceUpgrade) {
            upgradeDialog = null;
        }
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck != null) {
            AppPermissionHelper.with((FragmentActivity) activityWithCheck)
                    .requestPermission(new WriteStoragePermissionRequest(), new PermissionCallback() {
                        @Override
                        public void onGranted() {
                            //关闭log否则进度不显示
                            DownloadFileInfo downloadFileInfo = new DownloadFileInfo(downloadUrl);
                            DownloadFileHelper.INSTANCE.addDownloadFileCallback(new DownloadFileCallback() {
                                @Override
                                public void onDownloadStart(DownloadFileInfo downloadFileInfo) {
                                    OkHttpManager.INSTANCE.setLogSwitch(false);
                                    if (onAppUpgradeListener != null) {
                                        onAppUpgradeListener.onDownloadStart(downloadFileInfo);
                                    }
                                    if (needForceUpgrade && upgradeDialog != null) {
                                        upgradeDialog.setAlertContentTxt("开始下载")
                                                .setLeftNavTxt("升级中")
                                                .setLeftNavClickListener(null);
                                    }
                                }

                                @Override
                                public void onDownloadProgress(DownloadFileInfo downloadFileInfo, int progress) {
                                    notifyDownloadProgress(progress, false);
                                    if (onAppUpgradeListener != null) {
                                        onAppUpgradeListener.onDownloadProgress(progress);
                                    }
                                    if (needForceUpgrade && upgradeDialog != null) {
                                        upgradeDialog.setAlertContentTxt("正在下载 " + progress + "%")
                                                .setLeftNavTxt("升级中")
                                                .setLeftNavClickListener(null);
                                    }
                                }

                                @Override
                                public void onDownloadFail(DownloadFileInfo downloadFileInfo, String errorMsg) {
                                    //重新打开log
                                    OkHttpManager.INSTANCE.setLogSwitch(BuildConfig.DEBUG);
                                    notifyDownloadProgress(0, true);
                                    DownloadFileHelper.INSTANCE.removeDownloadFileCallback(this);
                                    isCheckUpgrade = false;
                                    if (onAppUpgradeListener != null) {
                                        onAppUpgradeListener.onDownloadError(errorMsg);
                                    }
                                    if (needForceUpgrade && upgradeDialog != null) {
                                        upgradeDialog.setAlertContentTxt("下载失败!")
                                                .setLeftNavTxt("重新下载")
                                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                                        downloadAppNewVersion(downloadUrl, true);
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onDownloadResult(DownloadFileInfo downloadFileInfo, String downloadResultMsg) {
                                    //重新打开log
                                    OkHttpManager.INSTANCE.setLogSwitch(BuildConfig.DEBUG);
                                    notifyDownloadProgress(100, true);
                                    DownloadFileHelper.INSTANCE.removeDownloadFileCallback(this);
                                    isCheckUpgrade = false;
                                    if (onAppUpgradeListener != null) {
                                        onAppUpgradeListener.onDownloadSuccess(downloadFileInfo, needForceUpgrade);
                                    }
                                    if (needForceUpgrade) {
                                        upgradeDialog.setAlertContentTxt("安装中,请稍等")
                                                .setLeftNavTxt("安装中")
                                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                                        //去静默安装
                                                        DeviceManager.INSTANCE.getDeviceInterface().silenceInstallApk(downloadFileInfo.getLocalUri());
                                                    }
                                                });
                                        //去静默安装
                                        DeviceManager.INSTANCE.getDeviceInterface().silenceInstallApk(downloadFileInfo.getLocalUri());
                                    }
                                }
                            });
                            DownloadFileHelper.INSTANCE.downloadFile(downloadFileInfo);
                        }
                    });
        }

    }

    /**
     * 升级成功回调
     */
    public void appUpgradeCallback() {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMap();
        paramsMap.put("machine_Number", DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        paramsMap.put("mode", "EquUpgCallback");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .appUpgradeCallback(ParamsUtils.signSortParamsMap(paramsMap))
                .enqueue(new DefaultRetrofitCallback<>());
    }

    public boolean isCheckUpgrade() {
        return isCheckUpgrade;
    }

    private void notifyDownloadProgress(int progress, boolean isFinished) {
        Context context = AppManager.INSTANCE.getApplication();
        if (NotificationUtil.createAndCheckUpdateNotificationEnabled(context)) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (isFinished) {
                if (notificationManager != null) {
                    notificationManager.cancel(NOTIFY_ID);
                }
            } else {
                NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context, NotificationUtil.UPDATE_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.icon_launcher)
                        .setContentTitle("智慧超市")
                        .setContentText("正在下载" + progress + "%")
                        .setAutoCancel(true)
                        .setProgress(100, progress, false);
                if (notificationManager != null) {
                    notificationManager.notify(NOTIFY_ID, notificationCompatBuilder.build());
                }
            }
        }
    }

    @Override
    public void onClear() {
    }

    public interface OnAppUpgradeListener {
        void onCheckVersionEnd(String msg);

        void onCheckVersionStart();

        void onCheckVersionError(String msg);

        void onNoVersionUpgrade();

        default void onDownloadStart(DownloadFileInfo downloadFileInfo) {
        }

        default void onDownloadProgress(int progress) {
        }

        default void onDownloadError(String msg) {
        }

        default void onDownloadSuccess(DownloadFileInfo downloadFileInfo, boolean isForceUpdate) {
        }
    }
}
