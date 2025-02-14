package com.stkj.infocollect.setting.ui.fragment;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.common.crash.XCrashHelper;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FileUtils;
import com.stkj.infocollect.BuildConfig;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.callback.OnUploadLogListener;
import com.stkj.infocollect.base.model.CommonExpandItem;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.infocollect.base.upload.UploadLogHelper;
import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.infocollect.home.callback.OnGetStoreInfoListener;
import com.stkj.infocollect.home.helper.ScreenProtectHelper;
import com.stkj.infocollect.home.model.StoreInfo;
import com.stkj.infocollect.login.callback.LoginCallback;
import com.stkj.infocollect.login.helper.LoginHelper;
import com.stkj.infocollect.setting.data.DeviceSettingMMKV;
import com.stkj.infocollect.setting.helper.AppUpgradeHelper;
import com.stkj.infocollect.setting.helper.CrashLogHelper;
import com.stkj.infocollect.setting.helper.StoreInfoHelper;
import com.stkj.common.download.DownloadFileInfo;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import xcrash.TombstoneManager;

/**
 * 本机设置
 */
public class TabDeviceSettingFragment extends BaseRecyclerFragment implements OnGetStoreInfoListener, AppUpgradeHelper.OnAppUpgradeListener {

    private ScrollView svContent;
    private TextView tvDeviceId;
    private TextView tvDeviceName;
    private TextView tvVersionName;
    private ShapeSelectTextView stvCheckUpgrade;
    private FrameLayout flScreenProtect;
    private ShapeTextView stvScreenProtect;
    private ImageView ivScreenProtect;
    private ProgressBar pbCheckVersion;
    private TextView tvCheckVersionLoading;
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private ShapeSelectTextView stvChangeUser;
    private ShapeSelectTextView stvExportCrashLog;
    private ShapeSelectTextView stvClearCrashLog;
    private ImageView ivSwitchSysLog;
    private ShapeSelectTextView stvUploadSysLog;
    private ProgressBar pbUploadSysLogLoading;
    private ShapeSelectTextView stvClearCameraCache;
    private ProgressBar pbClearCameraCacheLoading;

    @Override
    protected int getLayoutResId() {
        return com.stkj.infocollect.R.layout.fragment_tab_device_setting;
    }

    @Override
    protected void initViews(View rootView) {
        stvUploadSysLog = (ShapeSelectTextView) findViewById(R.id.stv_upload_sys_log);
        pbUploadSysLogLoading = (ProgressBar) findViewById(R.id.pb_upload_sys_log_loading);
        svContent = (ScrollView) findViewById(R.id.sv_content);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        ivUserAvatar = (ImageView) findViewById(R.id.iv_user_avatar);
        stvChangeUser = (ShapeSelectTextView) findViewById(R.id.stv_change_user);
        stvChangeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeUserDialog();
            }
        });
        pbCheckVersion = (ProgressBar) findViewById(R.id.pb_check_version_loading);
        tvCheckVersionLoading = (TextView) findViewById(R.id.tv_check_version_loading);
        tvDeviceId = (TextView) findViewById(R.id.tv_device_id);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvVersionName = (TextView) findViewById(R.id.tv_version_name);
        tvVersionName.setText("慧餐宝v" + BuildConfig.VERSION_NAME);
        stvCheckUpgrade = (ShapeSelectTextView) findViewById(R.id.stv_check_upgrade);
        RxView.clicks(stvCheckUpgrade)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        AppUpgradeHelper appUpgradeHelper = mActivity.getWeakRefHolder(AppUpgradeHelper.class);
                        appUpgradeHelper.setOnAppUpgradeListener(TabDeviceSettingFragment.this);
                        appUpgradeHelper.checkAppVersion();
                    }
                });
        flScreenProtect = (FrameLayout) findViewById(R.id.fl_screen_protect);
        stvScreenProtect = (ShapeTextView) findViewById(R.id.stv_screen_protect);
        ivScreenProtect = (ImageView) findViewById(R.id.iv_screen_protect);
        tvDeviceId.setText(BuildConfig.machineNumber);
        //屏幕保护
        int screenProtectTime = DeviceSettingMMKV.getScreenProtectTime();
        if (screenProtectTime == 10) {
            stvScreenProtect.setText("10秒钟(仅供测试)");
        } else if (screenProtectTime == 3 * 60) {
            stvScreenProtect.setText("3分钟");
        } else if (screenProtectTime == 5 * 60) {
            stvScreenProtect.setText("5分钟");
        } else if (screenProtectTime == 10 * 60) {
            stvScreenProtect.setText("10分钟");
        } else {
            stvScreenProtect.setText("永不");
        }
        List<CommonExpandItem> expandItemList = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            expandItemList.add(new CommonExpandItem(10, "10秒钟(仅供测试)"));
        }
        expandItemList.add(new CommonExpandItem(3 * 60, "3分钟"));
        expandItemList.add(new CommonExpandItem(5 * 60, "5分钟"));
        expandItemList.add(new CommonExpandItem(10 * 60, "10分钟"));
        expandItemList.add(new CommonExpandItem(-1, "永不"));
        flScreenProtect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivScreenProtect.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                commonExpandListPopWindow.setWidth(flScreenProtect.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivScreenProtect.setSelected(false);
                        stvScreenProtect.setText(commonExpandItem.getName());
                        int countDownTime = commonExpandItem.getTypeInt();
                        DeviceSettingMMKV.putScreenProtectTime(countDownTime);
                        ScreenProtectHelper screenProtectHelper = mActivity.getWeakRefHolder(ScreenProtectHelper.class);
                        screenProtectHelper.setScreenProtectTime(countDownTime);
                        AppToast.toastMsg("设置已生效");
                    }
                });
                commonExpandListPopWindow.setExpandItemList(expandItemList);
                commonExpandListPopWindow.showAsDropDown(flScreenProtect);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivScreenProtect.setSelected(false);
                    }
                });
            }
        });
        stvExportCrashLog = (ShapeSelectTextView) findViewById(R.id.stv_export_crash_log);
        stvExportCrashLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashLogHelper.saveCrashLogToSDCard(mActivity);
            }
        });
        stvClearCrashLog = (ShapeSelectTextView) findViewById(R.id.stv_clear_crash_log);
        stvClearCrashLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashLogHelper.clearCrashLog(mActivity);
            }
        });
        //测试崩溃日志
//        stvExportCrashLog.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                XCrash.testJavaCrash(false);
//                return true;
//            }
//        });
//        stvClearCrashLog.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                XCrash.testNativeCrash(false);
//                return true;
//            }
//        });
        //系统日志开关
        ivSwitchSysLog = (ImageView) findViewById(R.id.iv_switch_sys_log);
        ivSwitchSysLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadLogHelper uploadLogHelper = mActivity.getWeakRefHolder(UploadLogHelper.class);
                if (uploadLogHelper.isUploadingLog()) {
                    AppToast.toastMsg("日志上传中,请稍等");
                    return;
                }
                boolean openSyslog = !ivSwitchSysLog.isSelected();
                ivSwitchSysLog.setSelected(openSyslog);
                DeviceSettingMMKV.putOpenSysLog(openSyslog);
                LogHelper.setLogEnable(openSyslog);
            }
        });
        boolean openSysLog = DeviceSettingMMKV.isOpenSysLog();
        ivSwitchSysLog.setSelected(openSysLog);
        stvUploadSysLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadLogHelper uploadLogHelper = mActivity.getWeakRefHolder(UploadLogHelper.class);
                if (uploadLogHelper.isUploadingLog()) {
                    AppToast.toastMsg("日志上传中");
                    return;
                }
                uploadLogHelper.setUploadLogListener(uploadLogListener);
                uploadLogHelper.uploadXLogFile();
            }
        });
        pbClearCameraCacheLoading = (ProgressBar) findViewById(R.id.pb_clear_camera_cache_loading);
        stvClearCameraCache = (ShapeSelectTextView) findViewById(R.id.stv_clear_camera_cache);
        stvClearCameraCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCameraCache();
            }
        });
    }

    private void deleteCameraCache() {
        //正在清理中
        if (!stvClearCameraCache.isShapeSelect()) {
            return;
        }
        pbClearCameraCacheLoading.setVisibility(View.VISIBLE);
        stvClearCameraCache.setShapeSelect(false);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                        FileUtils.deleteDirectory(StorageHelper.getExternalCacheDirPath());
                        FileUtils.deleteDirectory(StorageHelper.getExternalShareDirPath());
                        emitter.onNext(true);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDisposeUtils.onDestroyDispose(mActivity))
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                        stvClearCameraCache.setShapeSelect(true);
                        pbClearCameraCacheLoading.setVisibility(View.GONE);
                        AppToast.toastMsg("清理完成");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        stvClearCameraCache.setShapeSelect(true);
                        pbClearCameraCacheLoading.setVisibility(View.GONE);
                        AppToast.toastMsg("清理失败");
                    }
                });
    }

    private void showChangeUserDialog() {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("切换账号会清理用户信息,请确认?")
                .setLeftNavTxt("确定")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        LoginHelper.INSTANCE.clearUserInfo();
                        LoginHelper.INSTANCE.handleLoginValid(false);
                    }
                })
                .setRightNavTxt("取消")
                .show(mActivity);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        //设备信息
        StoreInfoHelper storeInfoHelper = mActivity.getWeakRefHolder(StoreInfoHelper.class);
        StoreInfo storeInfo = storeInfoHelper.getStoreInfo();
        if (storeInfo != null) {
            tvDeviceName.setText(storeInfo.getDeviceName());
        } else {
            storeInfoHelper.requestStoreInfo();
            storeInfoHelper.addGetStoreInfoListener(this);
        }
        //检查更新状态
        AppUpgradeHelper appUpgradeHelper = mActivity.getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.setOnAppUpgradeListener(this);
        boolean checkUpgrade = appUpgradeHelper.isCheckUpgrade();
        if (checkUpgrade) {
            pbCheckVersion.setVisibility(View.VISIBLE);
            tvCheckVersionLoading.setText("正在更新");
            stvCheckUpgrade.setShapeSelect(false);
        } else {
            pbCheckVersion.setVisibility(View.GONE);
            tvCheckVersionLoading.setVisibility(View.GONE);
            stvCheckUpgrade.setShapeSelect(true);
        }
        tvUserName.setText(LoginHelper.INSTANCE.getAccount());
        String faceImg = LoginHelper.INSTANCE.getFaceImg();
        if (TextUtils.isEmpty(faceImg)) {
            ivUserAvatar.setImageResource(R.mipmap.icon_face_default);
        } else {
            GlideApp.with(mActivity).load(faceImg).placeholder(R.mipmap.icon_face_default).circleCrop().into(ivUserAvatar);
        }
        LoginHelper.INSTANCE.addLoginCallback(loginCallback);
        //上传日志
        UploadLogHelper uploadLogHelper = mActivity.getWeakRefHolder(UploadLogHelper.class);
        if (uploadLogHelper.isUploadingLog()) {
            stvUploadSysLog.setShapeSelect(false);
            pbUploadSysLogLoading.setVisibility(View.VISIBLE);
        } else {
            stvUploadSysLog.setShapeSelect(true);
            pbUploadSysLogLoading.setVisibility(View.GONE);
        }
    }

    private LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess() {
            if (tvUserName != null) {
                tvUserName.setText(LoginHelper.INSTANCE.getAccount());
                String faceImg = LoginHelper.INSTANCE.getFaceImg();
                if (TextUtils.isEmpty(faceImg)) {
                    ivUserAvatar.setImageResource(R.mipmap.icon_face_default);
                } else {
                    GlideApp.with(mActivity).load(faceImg).placeholder(R.mipmap.icon_face_default).circleCrop().into(ivUserAvatar);
                }
            }
        }
    };

    private OnUploadLogListener uploadLogListener = new OnUploadLogListener() {

        @Override
        public void onUploadStart() {
            if (pbUploadSysLogLoading != null) {
                pbUploadSysLogLoading.setVisibility(View.VISIBLE);
                stvUploadSysLog.setShapeSelect(false);
            }
        }

        @Override
        public void onUploadLogSuccess() {
            AppToast.toastMsg("上传日志完成");
            if (pbUploadSysLogLoading != null) {
                pbUploadSysLogLoading.setVisibility(View.GONE);
                stvUploadSysLog.setShapeSelect(true);
            }
        }

        @Override
        public void onUploadLogError(String msg) {
            CommonDialogUtils.showTipsDialog(mActivity, "上传日志失败:" + msg);
            if (pbUploadSysLogLoading != null) {
                pbUploadSysLogLoading.setVisibility(View.GONE);
                stvUploadSysLog.setShapeSelect(true);
            }
        }
    };

    @Override
    public void onDestroy() {
        LoginHelper.INSTANCE.removeLoginCallback(loginCallback);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        StoreInfoHelper storeInfoHelper = mActivity.getWeakRefHolder(StoreInfoHelper.class);
        storeInfoHelper.removeGetStoreInfoListener(this);
        AppUpgradeHelper appUpgradeHelper = mActivity.getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.setOnAppUpgradeListener(null);
        UploadLogHelper uploadLogHelper = mActivity.getWeakRefHolder(UploadLogHelper.class);
        uploadLogHelper.setUploadLogListener(null);
        super.onDetach();
    }

    @Override
    public void onGetStoreInfo(StoreInfo storeInfo) {
        tvDeviceName.setText(storeInfo.getDeviceName());
    }

    @Override
    public void onCheckVersionStart() {
        pbCheckVersion.setVisibility(View.VISIBLE);
        tvCheckVersionLoading.setText("正在更新");
        stvCheckUpgrade.setShapeSelect(false);
    }

    @Override
    public void onCheckVersionEnd(String msg) {
        pbCheckVersion.setVisibility(View.GONE);
        tvCheckVersionLoading.setVisibility(View.GONE);
        stvCheckUpgrade.setShapeSelect(true);
        if (!TextUtils.isEmpty(msg)) {
            CommonDialogUtils.showTipsDialog(mActivity, msg);
        }
    }

    @Override
    public void onCheckVersionError(String msg) {
        pbCheckVersion.setVisibility(View.GONE);
        tvCheckVersionLoading.setVisibility(View.GONE);
        stvCheckUpgrade.setShapeSelect(true);
        CommonDialogUtils.showTipsDialog(mActivity, "检查更新失败:" + msg);
    }

    @Override
    public void onNoVersionUpgrade() {
        pbCheckVersion.setVisibility(View.GONE);
        tvCheckVersionLoading.setVisibility(View.GONE);
        stvCheckUpgrade.setShapeSelect(true);
        CommonDialogUtils.showTipsDialog(mActivity, "已经是最新版本了");
    }

    @Override
    public void onDownloadProgress(int progress) {
        pbCheckVersion.setVisibility(View.VISIBLE);
        tvCheckVersionLoading.setVisibility(View.VISIBLE);
        stvCheckUpgrade.setShapeSelect(false);
        tvCheckVersionLoading.setText("正在下载 " + progress + "%");
    }

    @Override
    public void onDownloadError(String msg) {
        pbCheckVersion.setVisibility(View.GONE);
        tvCheckVersionLoading.setVisibility(View.GONE);
        stvCheckUpgrade.setShapeSelect(true);
        AppToast.toastMsg("下载app失败");
    }

    @Override
    public void onDownloadSuccess(DownloadFileInfo downloadFileInfo, boolean isForceUpdate) {
        pbCheckVersion.setVisibility(View.GONE);
        tvCheckVersionLoading.setVisibility(View.GONE);
        stvCheckUpgrade.setShapeSelect(true);
        if (!isForceUpdate) {
            CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                    .setAlertTitleTxt("版本升级")
                    .setAlertContentTxt("新版本下完毕，点击更新")
                    .setLeftNavTxt("更新")
                    .setNeedHandleDismiss(true)
                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            //去安装
                            alertDialogFragment.dismiss();
                            String apkPath = downloadFileInfo.getLocalUri();
                            if (apkPath.startsWith("content:")) {
                                AndroidUtils.installApk(Uri.parse(apkPath));
                            } else {
                                AndroidUtils.installApk(new File(apkPath));
                            }
                        }
                    })
                    .setRightNavTxt("取消")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            alertDialogFragment.dismiss();
                        }
                    });
            commonAlertDialogFragment.show(mActivity);
        }
    }

}
